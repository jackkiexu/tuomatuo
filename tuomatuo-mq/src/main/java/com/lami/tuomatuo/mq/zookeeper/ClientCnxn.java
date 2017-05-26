package com.lami.tuomatuo.mq.zookeeper;

import com.lami.tuomatuo.mq.zookeeper.client.HostProvider;
import com.lami.tuomatuo.mq.zookeeper.client.ZooKeeperSaslClient;
import com.lami.tuomatuo.mq.zookeeper.server.ByteBufferInputStream;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooTrace;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class manages the socket i/o for this client. ClientCnxn maintains a list
 * of available servers to connect to and "transparently" switches servers it is
 * connected to as needed
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ClientCnxn {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCnxn.class);


    private static final String ZK_SASL_CLIENT_USERNAME =
            "zookeeper.sasl.client.username";

    /**
     * This controls whether automatic watch resetting is enabled.
     * Clients automatically reset watches during session reconnect, this
     * option allows the client to turn off this behavior by setting
     * the environment variable "zookeeper.disableAutoWatchReset" to true
     */
    private static boolean disableAutoWatchReset;

    static {
        // this var should not be public, but otw there is no wasy way
        // to test
        disableAutoWatchReset = Boolean.getBoolean("zookeeper.disableAutoWatchReset");
        LOG.info("zookeeper.disableAu");
    }


    static class AuthData {
        AuthData(String scheme, byte data[]){
            this.scheme = scheme;
            this.data = data;
        }

        String scheme;
        byte data[];
    }

    private final CopyOnWriteArraySet<AuthData> authInfo = new CopyOnWriteArraySet<>();

    /**
     * These are the packets that have been sent and are waiting for a response
     */
    private final LinkedList<Packet> pendingQueue = new LinkedList<>();

    /**
     * These are the packets that need to be sent
     */
    private final LinkedList<Packet> outgoingQueue = new LinkedList<>();

    private int connectTimeout;

    /**
     * The timeout in ms the client negotiated with the server. This is the
     * "real" timeout, not the timeout request by the client(which may have
     * been increased/decreased by the server which applies bounds to this
     * value)
     */
    private volatile int negotiatedSessionTimeout;

    private int readTimeout;


    private final int sessionTimeout;

    private final ZooKeeper zooKeeper;

    private final ClientWatchManager watcher;

    private long sessionId;

    private byte sessionPasswd[] = new byte[16];

    /**
     * If true, the connection is allowed to go to r-o mode. This field's value
     * is sent, besides other data, during session creation handshake. If the
     * server on the other side of the wire is partitioned it'll accept
     * read-only client only
     */
    private boolean readOnly;

    final String chrootPath;

    final SendThread sendThread;

    final EventThread eventThread;

    /**
     * Set to true when close is called. Latches the connection such that we
     * don't attempt to re-connect to the server if in the middle of closing the
     * connection (client sends session disconnect to server as part of close
     * operation)
     */
    private volatile boolean closing = true;

    /**
     * A set of ZooKeeper hosts this client could connect to.
     */
    private final HostProvider hostProvider;


    /**
     * Is set to true when a connection to a r/w server is established for the
     * first time; never changed afterwards
     * Is used to handle situations when client without sessionId connects to a
     * read-only server. Such client receives "fake" sessionId from read-only
     * server, but this sessionId is invalid for other servers. So when such
     * client finds r/w server, it sends 0 instead of fake sessionId during
     * connection handshake and establishes new, valid session
     * If this field is false (which implies we haven't seen r/w server before)
     * then non-zero sessionId is fake, otherwise it is valid
     */
    public volatile boolean seenRWServerBefore = false;

    public ZooKeeperSaslClient zooKeeperSaslClient;


    public long getSessionId() {
        return sessionId;
    }

    public byte[] getSessionPasswd() {
        return sessionPasswd;
    }


    public int getSessionTimeout(){
        return sessionTimeout;
    }

    public int getNegotiatedSessionTimeout() {
        return negotiatedSessionTimeout;
    }

    public ClientCnxn(String chrootPath, HostProvider hostProvider, int sessionTimeout,
                      ZooKeeper zooKeeper, ClientWatchManager watcher, ClientCnxnSocket clientCnxnSocket,
                      boolean canBeReadOnly)throws IOException{
        this(chrootPath, hostProvider, sessionTimeout, zooKeeper, watcher,
                clientCnxnSocket, 0, new byte[16], canBeReadOnly);
    }

    /**
     * Creates a connection object. The actual network connect doesn't get
     * established until needed. The start() instance method must be called
     * subsequent to construction
     *
     * @param chrootPath
     *      the chroot of this client. Should be removed from this Class
     * @param hostProvider
     *      the list of ZooKeeper servers to connect to
     * @param sessionTimeout
     *      the timeout for connection
     * @param zooKeeper
     *      the zookeeper object that this connection is related to
     * @param watcher
     *      watcher for this connection
     * @param clientCnxnSocket
     *      the socket implementation used
     * @param sessionId
     *      session id if re-establishing session
     * @param sessionPasswd
     *      session passwd if re-establishing session
     * @param canBeReadOnly
     *      whether the connection is allowed to go to read-only
     *      mode in case of partitioning
     * @throws IOException
     */
    public ClientCnxn(String chrootPath, HostProvider hostProvider, int sessionTimeout, ZooKeeper zooKeeper,
                      ClientWatchManager watcher, ClientCnxnSocket clientCnxnSocket,
                      long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)throws IOException{
        this.zooKeeper = zooKeeper;
        this.watcher = watcher;
        this.sessionId = sessionId;
        this.sessionPasswd = sessionPasswd;
        this.sessionTimeout = sessionTimeout;
        this.hostProvider = hostProvider;
        this.chrootPath = chrootPath;

        connectTimeout = sessionTimeout / hostProvider.size();
        readTimeout = sessionTimeout * 2 / 3;
        readOnly = canBeReadOnly;

        sendThread = new SendThread(clientCnxnSocket);
        eventThread = new EventThread();

    }

    /**
     * tests use this to check on reset of watches
     * @return
     */
    public static boolean getDisableAutoResetWatch(){
        return disableAutoWatchReset;
    }

    public static void setDisableAutoWatchReset(boolean b) {
        disableAutoWatchReset = b;
    }

    public void start(){
        sendThread.start();
        eventThread.start();
    }

    private Object eventOfDeath = new Object();

    private final static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.error("from" + t.getName(), e);
        }
    };

    private static class WatcherSetEventPair {
        private final Set<Watcher> watchers;
        private final WatchedEvent event;

        public WatcherSetEventPair(Set<Watcher> watchers, WatchedEvent event) {
            this.watchers = watchers;
            this.event = event;
        }
    }

    /**
     * Guard against creating "-EventThread-EventThread-EventThread-..." thread
     * names when ZooKeeper object is being created from within a watcher
     * See ZooKeeper-795 for detals
     * @param suffix
     * @return
     */
    private static String makeThreadName(String suffix){
        String name = Thread.currentThread().getName().replaceAll("-EventThread", "");
        return name + suffix;
    }


    private void finishPacket(Packet p){
        if(p.watchRegistration != null){
            p.watchRegistration.register(p.replyHeader.getErr());
        }

        if(p.cb == null){
            synchronized (p){
                p.finished = true;
                p.notifyAll();
            }
        }else{
            p.finished = true;
            eventThread.queuePacket(p);
        }
    }


    private void conLossPacket(Packet p){
        if(p.replyHeader == null){
            return;
        }
        switch (state){
            case AUTH_FAILED:
                p.replyHeader.setErr(KeeperException.Code.AUTHFAILED.intValue());
            case CLOSED:
                p.replyHeader.setErr(KeeperException.Code.SESSIONEXPIRED.intValue());
            default:
                p.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
        }
        finishPacket(p);
    }

    private volatile long lastZxid;

    public long getLastZxid() {
        return lastZxid;
    }

    static class EndOfStreamException extends IOException{

        private static final long serialVersionUID = 9188551248672651373L;

        public EndOfStreamException(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return "EndOfStreamException : " + getMessage();
        }
    }

    private static class SessionTimeoutException extends IOException{

        private static final long serialVersionUID = -1547708988721884037L;

        public SessionTimeoutException(String message) {
            super(message);
        }
    }

    private static class SessionExpiredException extends IOException{

        private static final long serialVersionUID = -7278573219696847737L;

        public SessionExpiredException(String message) {
            super(message);
        }
    }

    private static class RWServerFoundException extends IOException{

        private static final long serialVersionUID = 2816892115624061475L;

        public RWServerFoundException(String message) {
            super(message);
        }
    }




    public static final int packetLen = Integer.getInteger("jute.maxbuffer", 4096 * 1024);

    /**
     * This class services the outgoing request queue and generates the heart
     * beats. It also spawns the ReadThread
     */
    class SendThread extends ZooKeeperThread {
        private long lastPingSentNs;
        private final ClientCnxnSocket clientCnxnSocket;
        private Random r = new Random(System.nanoTime());
        private boolean isFirstConnect = true;


        public SendThread(ClientCnxnSocket clientCnxnSocket) {
            super(makeThreadName("-SendThread()"));
            state = ZooKeeper.States.CONNECTING;
            this.clientCnxnSocket = clientCnxnSocket;
            setUncaughtExceptionHandler(uncaughtExceptionHandler);
            setDaemon(true);
        }

        void readResponse(ByteBuffer incomingBuffer) throws IOException{
            ByteBufferInputStream bbis = new ByteBufferInputStream(incomingBuffer);
            BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
            ReplyHeader replyHeader = new ReplyHeader();

            replyHeader.deserialize(bbia, "header");
            if(replyHeader.getXid() == -2){
                // -2 is the xid for pings
                LOG.info("Got ping response for sessionId: 0x"
                        + Long.toHexString(sessionId)
                        + " after "
                        + (System.nanoTime() - lastPingSentNs) / 1000000
                        + " ms ");
                return;
            }

            if(replyHeader.getXid() == -4){
                // -4 is the xid for AuthPacket
                if(replyHeader.getErr() == KeeperException.Code.AUTHFAILED.intValue()){
                    state = ZooKeeper.States.AUTH_FAILED;
                    eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None,
                            Watcher.Event.KeeperState.AuthFailed, null));
                }

                return;
            }

            if(replyHeader.getXid() == -1){
                // -1 means notification
                LOG.info("Got notification sessionId: 0x", Long.toHexString(sessionId));

                WatcherEvent event = new WatcherEvent();
                event.deserialize(bbia, "response");

                // convert from a server path to a client path

                if(chrootPath != null){
                    String serverPath = event.getPath();
                    if(serverPath.compareTo(chrootPath) == 0){
                        event.setPath("/");
                    }
                    else if(serverPath.length() > chrootPath.length()){
                        event.setPath(serverPath.substring(chrootPath.length()));
                    }
                    else{
                        LOG.info("Got server path " + event.getPath() +
                                " which is too short for chroot path "
                                + chrootPath);
                    }
                }

                WatchedEvent we = new WatchedEvent(event);
                eventThread.queueEvent(we);
                return;

            }


            // If SASL authentication is currently in process, construct and
            // send a response packet immediately, rather than queuing a
            // response as with other packets
            if(clientTunneledAuthenticationInProcess()){
                GetSASLRequest request = new GetSASLRequest();
                request.deserialize(bbia, "token");
                zooKeeperSaslClient.respondToServer(request.getToken(),
                        ClientCnxn.this);
                return;
            }

            Packet packet;

            synchronized (pendingQueue){
                if(pendingQueue.size() == 0){
                    throw new IOException("Nothing in the queue, but got " +
                            replyHeader);
                }
                packet = pendingQueue.remove();
            }

            /**
             * Since requests are processed in order, we better get a response
             * to the first request
             */


            try{
                if(packet.requestHeader.getXid() != replyHeader.getXid()){
                    packet.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
                    throw new IOException("Xid out of order. Got Xid"
                    + replyHeader.getXid() + " with err " +
                    replyHeader.getErr() +
                    "expected Xid"
                    + packet.replyHeader.getXid()
                    + " for a packet with details : " +
                    packet);
                }

                packet.replyHeader.setXid(replyHeader.getXid());
                packet.replyHeader.setErr(replyHeader.getErr());
                packet.replyHeader.setZxid(replyHeader.getZxid());

                if(replyHeader.getZxid() > 0){
                    lastZxid = replyHeader.getZxid();
                }

                if (packet.response != null && replyHeader.getErr() == 0) {
                    packet.response.deserialize(bbia, "response");
                }




            }finally {
                finishPacket(packet);
            }




        }

        ZooKeeper.States getZkState(){
            return state;
        }


        ClientCnxnSocket getClientCnxnSocket(){
            return clientCnxnSocket;
        }


        void primeConnection() throws IOException{
            LOG.info("Socket connection established to" +
                    clientCnxnSocket.getRemoteSocketAddress()
                    + ". initiating session");
            isFirstConnect = false;
            long sessId = (seenRWServerBefore)? sessionId : 0;
            ConnectRequest conReq = new ConnectRequest(0, lastZxid,
                    sessionTimeout, sessId, sessionPasswd);
            synchronized (outgoingQueue){
                // we add backwards since we are pushing into the front
                // Only send if there's a pending watch
                if(!disableAutoWatchReset){
                    List<String> dataWatches = zooKeeper.getDataWatches();
                    List<String> existWatches = zooKeeper.getExistWatches();
                    List<String> childWatches = zooKeeper.getChildWatches();
                    if(!dataWatches.isEmpty()
                            || !existWatches.isEmpty() || !childWatches.isEmpty()){
                        SetWatches sw = new SetWatches(lastZxid,
                                prependChroot(dataWatches),
                                prependChroot(existWatches),
                                prependChroot(childWatches));
                        RequestHeader h = new RequestHeader();
                        h.setType(ZooDefs.OpCode.setWatches);
                        h.setXid(-8);
                        Packet packet = new Packet(h, new ReplyHeader(), sw, null, null);
                        outgoingQueue.addFirst(packet);
                    }
                }


                for(AuthData id : authInfo){
                    outgoingQueue.addFirst(new Packet(new RequestHeader(-4,
                            ZooDefs.OpCode.auth), null, new AuthPacket(0, id.scheme,
                            id.data), null, null));
                }
                outgoingQueue.addFirst(new Packet(null, null, conReq,
                        null, null, readOnly));
            }

            clientCnxnSocket.enableReadWriteOnly();
            LOG.info("Session establishment request sent on "
            + clientCnxnSocket.getRemoteSocketAddress());
        }



        private List<String> prependChroot(List<String> paths){
            if(chrootPath != null && !paths.isEmpty()){
                for(int i = 0; i < paths.size(); ++i){
                    String clientPath = paths.get(i);
                    String serverPath;
                    // handle clientPath = "/"
                    if(clientPath.length() == 1){
                        serverPath = chrootPath;
                    }else{
                        serverPath = chrootPath + clientPath;
                    }
                    paths.set(i, serverPath);
                }
            }

            return paths;
        }

        private void sendPing(){
            lastPingSentNs = System.nanoTime();
            RequestHeader h = new RequestHeader(-2, ZooDefs.OpCode.ping);
            queuePacket(h, null, null, null, null, null, null, null, null);
        }


        private InetSocketAddress rwServerAddress = null;
        private final static int minPingRwTimeout = 100;
        private final static int maxPingRwTimeout = 60000;

        private int pingRwTimeout = minPingRwTimeout;

        // Set to true if and only if constructor of ZooKeeperSaslClient
        // throws a LoginException
        private boolean saslLoginFailed = false;


        private void startConnect() throws IOException{
            state = ZooKeeper.States.CONNECTING;

            InetSocketAddress addr;
            if(rwServerAddress != null){
                addr = rwServerAddress;
                rwServerAddress = null;
            }
            else{
                addr = hostProvider.next(1000);
            }

            setName(getName().replaceAll("\\(.*\\)",
                    "(" + addr.getHostName() + ":" + addr.getPort() + ")"));

            if (ZooKeeperSaslClient.isEnabled()) {
                try {
                    String principalUserName = System.getProperty(
                            ZK_SASL_CLIENT_USERNAME, "zookeeper");
                    zooKeeperSaslClient =
                            new ZooKeeperSaslClient(
                                    principalUserName+"/"+addr.getHostName());
                } catch (LoginException e) {
                    // An authentication error occurred when the SASL client tried to initialize:
                    // for Kerberos this means that the client failed to authenticate with the KDC.
                    // This is different from an authentication error that occurs during communication
                    // with the Zookeeper server, which is handled below.
                    LOG.warn("SASL configuration failed: " + e + " Will continue connection to Zookeeper server without "
                            + "SASL authentication, if Zookeeper server allows it.");
                    eventThread.queueEvent(new WatchedEvent(
                            Watcher.Event.EventType.None,
                            Watcher.Event.KeeperState.AuthFailed, null));
                    saslLoginFailed = true;
                }
            }

            logStartConnect(addr);
            clientCnxnSocket.connect(addr);

        }


        private void logStartConnect(InetSocketAddress addr){
            String msg = "Opening socket connection to server " + addr;
            if(zooKeeperSaslClient != null){
                msg += "." + zooKeeperSaslClient.getConfigStatus();
            }
            LOG.info(msg);
        }

        private static final String RETRY_CONN_MSG =
                ", closing socket connection and attempting reconnect";


        @Override
        public void run() {
            clientCnxnSocket.introduce(this, sessionId);
            clientCnxnSocket.updateNow();
            clientCnxnSocket.updateLastSendAndHeard();
            int to;
            long lastPingRwServer = System.currentTimeMillis();
            final int MAX_SEND_PING_INTERVAL = 10000; // 10 seconds

            while(state.isAlive()){
                try{
                    if(!clientCnxnSocket.isConnected()){
                        if(!isFirstConnect){
                            try{
                                Thread.sleep(r.nextInt(1000));
                            }catch (Exception e){
                                LOG.warn("Unexpected exception ", e);
                            }
                        }
                        // don't re-establish connection if we are closing
                        if(closing || !state.isAlive()){
                            break;
                        }
                        startConnect();
                        clientCnxnSocket.updateLastSendAndHeard();
                    }

                    if(state.isConnected()){
                        // determine whether we need to send an AuthFailed event
                        if(zooKeeperSaslClient != null){
                            boolean sendAuthEvent = false;
                            if(zooKeeperSaslClient.getSaslState() == null){
                                try{
                                    zooKeeperSaslClient.initialize(ClientCnxn.this);
                                }catch (Exception e){
                                    LOG.info("SASL authentication with ZooKeeper Quorum memeber failed: " + e);
                                    state = ZooKeeper.States.AUTH_FAILED;
                                    sendAuthEvent = true;
                                }
                            }

                            Watcher.Event.KeeperState authState = zooKeeperSaslClient.getKeeperState();

                            if(authState != null){
                                if(authState == Watcher.Event.KeeperState.AuthFailed){
                                    // An authentication error occurred during authentication with the ZooKeeper Server
                                    state = ZooKeeper.States.AUTH_FAILED;
                                    sendAuthEvent = true;
                                }else{
                                }
                            }

                            if(sendAuthEvent == true){
                                eventThread.queueEvent(new WatchedEvent(
                                        Watcher.Event.EventType.None,
                                        authState, null
                                ));
                            }
                        }
                        to = readTimeout - clientCnxnSocket.getIdleRecv();
                    }
                    else{
                        to = connectTimeout - clientCnxnSocket.getIdleRecv();
                    }


                }catch (Throwable e){
                    if(closing){

                    }else {
                        // this is ugly, you have a better way speak up
                        if(e instanceof SessionExpiredException){
                            LOG.info(e.getMessage() + ", Closing socket connection");
                        }
                        else if(e instanceof SessionTimeoutException){
                            LOG.info(e.getMessage() + RETRY_CONN_MSG);
                        }
                        else if(e instanceof EndOfStreamException){
                            LOG.info(e.getMessage() + RETRY_CONN_MSG);
                        }
                        else if(e instanceof RWServerFoundException){
                            LOG.info(e.getMessage());
                        }else{
                            LOG.warn(
                                    "Session 0x"
                                            + Long.toHexString(getSessionId())
                                            + " for server "
                                            + clientCnxnSocket.getRemoteSocketAddress()
                                            + ", unexpected error"
                                            + RETRY_CONN_MSG, e);
                        }

                        cleanup();

                        if(state.isAlive()){
                            eventThread.queueEvent(new WatchedEvent(
                                    Watcher.Event.EventType.None,
                                    Watcher.Event.KeeperState.Disconnected,
                                    null
                            ));
                            clientCnxnSocket.updateNow();
                            clientCnxnSocket.updateLastSendAndHeard();
                        }
                    }
                }
            }

            cleanup();
            clientCnxnSocket.close();
            if(state.isAlive()){
                eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None,
                        Watcher.Event.KeeperState.Disconnected, null));
                ZooTrace.logTraceMessage(LOG, ZooTrace.getTextTraceLevel(), "SendThread exitedloop");
            }
        }

        private void pingRWServer() throws RWServerFoundException{
            String result = null;
            InetSocketAddress addr = hostProvider.next(0);
            LOG.info("Checking server " + addr + " for being r/w " + " Timeout " );

            Socket sock = null;
            BufferedReader br = null;
            try{
                sock = new Socket(addr.getHostName(), addr.getPort());
                sock.setSoLinger(false, -1);
                sock.setSoTimeout(1000);
                sock.setTcpNoDelay(true);
                sock.getOutputStream().write("isro".getBytes());
                sock.getOutputStream().flush();
                sock.shutdownOutput();
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                result = br.readLine();
            }catch (Exception e){

            }finally {
                if(sock != null){
                    try {
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(br != null){
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if("rw".equals(result)){
                // save the found address so that it's used during the next
                // connection attempt
                rwServerAddress = addr;
                throw new RWServerFoundException("Majority server found at " + addr.getHostName() +
                        " : " + addr.getPort());
            }
        }

        private void cleanup(){
            clientCnxnSocket.cleanup();
            synchronized (pendingQueue){
                for(Packet p : pendingQueue){
                    conLossPacket(p);
                }
                pendingQueue.clear();
            }

            synchronized (outgoingQueue){
                for(Packet p : outgoingQueue){
                    conLossPacket(p);
                }
                outgoingQueue.clear();
            }
        }



        void onConnected(int _negotiatedSessionTimeout, long _sessionId,
                         byte[] _sessionPasswd, boolean isRO) throws IOException{
            negotiatedSessionTimeout = negotiatedSessionTimeout;
            if(negotiatedSessionTimeout <= 0){
                state = ZooKeeper.States.CLOSED;
                eventThread.queueEvent(new WatchedEvent(
                        Watcher.Event.EventType.None,
                        Watcher.Event.KeeperState.Expired, null
                ));
                eventThread.queueEventOfDeath();
                throw new SessionExpiredException("" +
                        "Unable to reconnect to ZooKeeper service, session 0x"
                        + Long.toHexString(sessionId) + " has expired");
            }

            if(!readOnly && isRO){
                LOG.info("Read/write client got connected to read-only server");
            }

            readTimeout = negotiatedSessionTimeout * 2 / 3;
            connectTimeout = negotiatedSessionTimeout / hostProvider.size();
            hostProvider.onConnected();
            sessionId = _sessionId;
            sessionPasswd = _sessionPasswd;
            state = (isRO)?
                    ZooKeeper.States.CONNECTEDREADONLY : ZooKeeper.States.CONNECTED;
            seenRWServerBefore |= !isRO;
            LOG.info("Session establishment complete on server "
                    + clientCnxnSocket.getRemoteSocketAddress()
                    + ", sessionId = 0x " + Long.toHexString(sessionId)
                    + ", negotiated timeout = " + negotiatedSessionTimeout
                    + (isRO ? " (READ-ONLY mode)" : ""));
            Watcher.Event.KeeperState eventState = (isRO)?
                    Watcher.Event.KeeperState.ConnectedReadOnly: Watcher.Event.KeeperState.SyncConnected;
            eventThread.queueEvent(new WatchedEvent(
                    Watcher.Event.EventType.None,
                    eventState, null
            ));
        }

        void close(){
            state = ZooKeeper.States.CLOSED;
            clientCnxnSocket.wakeupCnxn();
        }


        void testableCloseSocket() throws IOException{
            clientCnxnSocket.testableCloseSocket();
        }


        public boolean clientTunneledAuthenticationInProcess(){
            // 1. SASL client is disabled
            if(!ZooKeeperSaslClient.isEnabled()){
                return false;
            }

            // 2. SASL login failed
            if(saslLoginFailed = true){
                return false;
            }

            // 3. SendThread has not created the authenticating object yet
            // therefore authentication is (at the earliest stage of being) in progress
            if(zooKeeperSaslClient == null){
                return true;
            }

            // 4. authenticating object exists, so ask it for its progress
            return zooKeeperSaslClient.clientTunneledAuthenticationInProgress();
        }

        public void sendPacket(Packet p) throws IOException{
            clientCnxnSocket.sendPacket(p);
        }

    }


    class EventThread extends Thread{
        private final LinkedBlockingQueue<Object> waitingEvents = new LinkedBlockingQueue<>();

        /**
         * This is really the queued session state until the event
         * thread actually processes the event and hands it to the watcher.
         * But for all intents and purposes this is the state
         */
        private volatile Watcher.Event.KeeperState sessionState = Watcher.Event.KeeperState.Disconnected;


        private volatile boolean wasKilled = false;
        private volatile boolean isRunning = false;

        public EventThread() {
            super(makeThreadName("-EventThread"));
            setUncaughtExceptionHandler(uncaughtExceptionHandler);
            setDaemon(true);
        }


        public void queueEvent(WatchedEvent event){
            if(event.getType() == Watcher.Event.EventType.None
                    && sessionState == event.getState()){
                return;
            }
            sessionState = event.getState();

            // materialize the watchers based on the event
            WatcherSetEventPair pair = new WatcherSetEventPair(watcher.materialize(event.getState(),
                    event.getType(), event.getPath()), event);
            // queue the pair (watch set & event) for later processing
            waitingEvents.add(pair);
        }

        public void queuePacket(Packet packet){
            if(wasKilled){
                synchronized (waitingEvents){
                    if(isRunning){
                        waitingEvents.add(packet);
                    }else{
                        processEvent(packet);
                    }
                }
            }else{
                waitingEvents.add(packet);
            }
        }

        public void queueEventOfDeath(){
            waitingEvents.add(eventOfDeath);
        }


        @Override
        public void run() {
            try{
                isRunning = true;
                while(true){
                    Object event = waitingEvents.take();
                    if(event == eventOfDeath){
                        wasKilled = true;
                    }else{
                        processEvent(event);
                    }
                    if(wasKilled){
                        synchronized (waitingEvents){
                            if(waitingEvents.isEmpty()){
                                isRunning = false;
                                break;
                            }
                        }
                    }
                }
            }catch (InterruptedException e){
                LOG.info("Event thread exiting due to interruption", e);
            }

            LOG.info("EventThread shut down");
        }



        private void processEvent(Object event){
            try{
                if(event instanceof WatcherSetEventPair){
                    // each watcher will process the event
                    WatcherSetEventPair pair = (WatcherSetEventPair)event;
                    for(Watcher watcher : pair.watchers){
                        try{
                            watcher.process(pair.event);
                        }catch (Throwable t){
                            LOG.info("Error while calling watcher", t);
                        }
                    }
                }
                else{
                    Packet p = (Packet)event;
                    int rc = 0;
                    String clientPath = p.clientPath;
                    if(p.replyHeader.getErr() != 0){
                        rc = p.replyHeader.getErr();
                    }

                    if(p.cb == null){
                        LOG.info("Somehow a null cb got to EventThread");
                    }
                    else if(p.response instanceof ExistsResponse
                            || p.response instanceof SetDataResponse
                            || p.response instanceof SetACLResponse
                            ){
                        AsyncCallback.StatCallback cb = (AsyncCallback.StatCallback)p.cb;
                        if(rc == 0){
                            if(p.response instanceof ExistsResponse){
                                cb.processResult(rc, clientPath, p.ctx,
                                        ((ExistsResponse)p.response).getStat());
                            } else if (p.response instanceof SetDataResponse) {
                                cb.processResult(rc, clientPath, p.ctx,
                                        ((SetDataResponse) p.response)
                                                .getStat());
                            }
                            else if(p.response instanceof SetACLResponse){
                                cb.processResult(rc, clientPath, p.ctx, ((SetACLResponse)p.response).getStat());
                            }
                        } else{
                            cb.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if(p.response instanceof GetDataResponse){
                        AsyncCallback.DataCallback cb = (AsyncCallback.DataCallback)p.cb;
                        GetDataResponse rsp = (GetDataResponse)p.response;
                        if(rc == 0){
                            cb.processResult(rc, clientPath, p.ctx, rsp.getData(), rsp.getStat());
                        }else{
                            cb.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if(p.response instanceof GetACLResponse){
                        AsyncCallback.ACLCallback cb = (AsyncCallback.ACLCallback)p.cb;
                        GetACLResponse rsp = (GetACLResponse)p.response;
                        if(rc == 0){
                            cb.processResult(rc, clientPath, p.ctx, rsp.getAcl(), rsp.getStat());
                        }else{
                            cb.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if(p.response instanceof GetChildrenResponse){
                        AsyncCallback.ChildrenCallback cb = (AsyncCallback.ChildrenCallback)p.cb;
                        GetChildrenResponse rsp = (GetChildrenResponse)p.response;
                        if(rc == 0){
                            cb.processResult(rc, clientPath, p.ctx, rsp.getChildren());
                        }else{
                            cb.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if(p.response instanceof GetChildren2Response){
                        AsyncCallback.Children2Callback cb = (AsyncCallback.Children2Callback)p.cb;
                        GetChildren2Response rsp = (GetChildren2Response)p.response;
                        if(rc == 0){
                            cb.processResult(rc, clientPath, p.ctx, rsp.getChildren(), rsp.getStat());
                        }else{
                            cb.processResult(rc, clientPath, p.ctx, null, null);
                        }
                    }
                    else if(p.response instanceof CreateResponse){
                        AsyncCallback.StringCallback cb = (AsyncCallback.StringCallback)p.cb;
                        CreateResponse rsp = (CreateResponse)p.response;
                        if(rc == 0){
                            cb.processResult(rc, clientPath, p.ctx,
                                    (chrootPath == null
                                            ? rsp.getPath()
                                            : rsp.getPath()
                                            .substring(chrootPath.length())));
                        }else{
                            cb.processResult(rc, clientPath, p.ctx, null);
                        }
                    }
                    else if(p.cb instanceof AsyncCallback.VoidCallback){
                        AsyncCallback.VoidCallback cb = (AsyncCallback.VoidCallback)p.cb;
                        cb.processResult(rc, clientPath, p.ctx);
                    }
                }

            }catch (Throwable t){
                LOG.info("Caught unexpected throwable", t);
            }
        }
    }





    /**
     * This class allows us to pass the headers and the repevant records around
     */
    static class Packet {
        RequestHeader requestHeader;

        ReplyHeader replyHeader;

        Record request;

        Record response;

        ByteBuffer bb;

        /** Client's view of the path (may differ use to chroot) */
        String clientPath;

        /** Server's view of the path (may differ due to chroot) */
        String serverPath;

        boolean finished;

        AsyncCallback cb;

        Object ctx;

        ZooKeeper.WatchRegistration watchRegistration;

        public boolean readOnly;

        public Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, ZooKeeper.WatchRegistration watchRegistration) {
            this.requestHeader = requestHeader;
            this.replyHeader = replyHeader;
            this.request = request;
            this.response = response;
            this.watchRegistration = watchRegistration;
            this.readOnly = false;
        }

        public Packet(RequestHeader requestHeader, ReplyHeader replyHeader,
                      Record request, Record response,
                      ZooKeeper.WatchRegistration watchRegistration, boolean readOnly) {
            this.requestHeader = requestHeader;
            this.replyHeader = replyHeader;
            this.request = request;
            this.response = response;
            this.watchRegistration = watchRegistration;
            this.readOnly = readOnly;
        }

        public void createBB(){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeInt(-1, "len"); // We'll fill this in later

                if(requestHeader != null){
                    requestHeader.serialize(boa, "header");
                }
                if (request instanceof ConnectRequest) {
                    request.serialize(boa, "connect");
                    // append "am I allowed to be readonly" flag
                    boa.writeBool(readOnly, "readOnly");
                }
                else if(request != null){
                    request.serialize(boa, "request");
                }

                baos.close();
                this.bb = ByteBuffer.wrap(baos.toByteArray());
                this.bb.putInt(this.bb.capacity() - 4);
                this.bb.rewind();
            }catch (IOException e){
                LOG.info("Ignoring unexpected exception", e);
            }
        }


        @Override
        public String toString() {
            return "Packet{" +
                    "requestHeader=" + requestHeader +
                    ", replyHeader=" + replyHeader +
                    ", request=" + request +
                    ", response=" + response +
                    ", bb=" + bb +
                    ", clientPath='" + clientPath + '\'' +
                    ", serverPath='" + serverPath + '\'' +
                    ", finished=" + finished +
                    ", cb=" + cb +
                    ", ctx=" + ctx +
                    ", watchRegistration=" + watchRegistration +
                    ", readOnly=" + readOnly +
                    '}';
        }
    }






    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        SocketAddress local = sendThread.getClientCnxnSocket().getLocalSocketAddress();
        SocketAddress remote = sendThread.getClientCnxnSocket().getRemoteSocketAddress();
        sb
                .append("sessionid:0x").append(Long.toHexString(getSessionId()))
                .append(" local:").append(local)
                .append(" remoteserver:").append(remote)
                .append(" lastZxid:").append(lastZxid)
                .append(" xid:").append(xid)
                .append(" sent:").append(sendThread.getClientCnxnSocket().getSentCount())
                .append(" recv:").append(sendThread.getClientCnxnSocket().getRecvCount())
                .append(" queuedpkts:").append(outgoingQueue.size())
                .append(" pendingresp:").append(pendingQueue.size())
                .append(" queuedevents:").append(eventThread.waitingEvents.size());

        return sb.toString();
    }


    /**
     * Shutdown the send/event threads
     */
    public void disconnect(){
        LOG.info("Disconnecting client for session:0x", Long.toHexString(getSessionId()));

        sendThread.close();
        eventThread.queueEventOfDeath();
    }

    /**
     * Close the connection, which includes; send session disconnect to the
     * server, shutdown the send/event threads
     * @throws IOException
     */
    public void close() throws IOException{
        LOG.info("Closing client for session: 0x", Long.toHexString(getSessionId()));

        try{
            RequestHeader h = new RequestHeader();
            h.setType(ZooDefs.OpCode.closeSession);

            submitRequest(h, null, null, null);
        }catch (Exception e){

        }finally {
            disconnect();
        }
    }



    private int xid = 1;

    private volatile ZooKeeper.States state = ZooKeeper.States.NOT_CONNECTED;

    synchronized public int getXid(){
        return xid++;
    }

    public ReplyHeader submitRequest(RequestHeader h, Record request,
                                     Record response, ZooKeeper.WatchRegistration watchRegistration)
        throws InterruptedException{
        ReplyHeader r = new ReplyHeader();
        Packet packet = queuePacket(h, r, request, response, null, null, null,
                                null, watchRegistration);
        synchronized (packet){
            while (!packet.finished){
                packet.wait();
            }
        }
        return r;
    }


    public void enableWrite(){
        sendThread.getClientCnxnSocket().enableWrite();
    }

    public void sendPacket(Record request, Record response, AsyncCallback cb, int opCode)
        throws IOException{
        // Generate XId now because it will be sent immediately
        // by call to sendThread.sendPacket() below
        int xid = getXid();
        RequestHeader h = new RequestHeader();
        h.setXid(xid);
        h.setType(opCode);

        ReplyHeader r = new ReplyHeader();
        r.setXid(xid);

        Packet p = new Packet(h, r, request, response, null, false);
        p.cb = cb;
        sendThread.sendPacket(p);
    }

    Packet queuePacket(RequestHeader h, ReplyHeader r, Record request,
                       Record respose, AsyncCallback cb, String clientPath,
                       String serverPath, Object ctx, ZooKeeper.WatchRegistration watchRegistration){
        Packet packet = null;

        // Note that we do not generate the Xid for the packet yet. It is
        // generated later at send-time, by an implementation of ClientCnxnSocket::doIO()
        // where the packet is actually sent
        synchronized (outgoingQueue){
            packet = new Packet(h, r, request, respose, watchRegistration);
            packet.cb = cb;
            packet.ctx = ctx;
            packet.clientPath = clientPath;
            packet.serverPath = serverPath;
            if(!state.isAlive() || closing){
                conLossPacket(packet);              // 若 zkClient 与 Server 之间失去连接, 就会产生 CONNECTIONLOSS 异常
            }else{
                // If the client is asking to close the session then
                // mark as closing
                if(h.getType() == ZooDefs.OpCode.closeSession){
                    closing = true;
                }
                outgoingQueue.add(packet);
            }
        }

        sendThread.getClientCnxnSocket().wakeupCnxn();
        return packet;
    }


    public void addAuthInfo(String scheme, byte auth[]){
        if(!state.isAlive()){
            return;
        }
        authInfo.add(new AuthData(scheme, auth));
        queuePacket(new RequestHeader(-4, ZooDefs.OpCode.auth), null,
                new AuthPacket(0, scheme, auth), null, null, null, null,
                null, null);
    }

    ZooKeeper.States getState(){
        return state;
    }
}
