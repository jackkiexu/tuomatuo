package com.lami.tuomatuo.mq.zookeeper;

import com.lami.tuomatuo.mq.zookeeper.client.HostProvider;
import com.lami.tuomatuo.mq.zookeeper.client.ZooKeeperSaslClient;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
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




    private volatile long lastZxid;

    public static final int packetLen = Integer.getInteger("jute.maxbuffer", 4096 * 1024);

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

    private int xid = 1;

    private volatile ZooKeeper.States state = ZooKeeper.States.NOT_CONNECTED;


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

    public int getNegotiatedSessionTimeout() {
        return negotiatedSessionTimeout;
    }


    public ClientCnxn(String chrootPath, HostProvider hostProvider, int sessionTimeout,
                      ZooKeeper zooKeeper, ClientWatchManager watcher, ClientCnxnSocket clientCnxnSocket,
                      boolean canBeReadOnly)throws IOException{
        this(chrootPath, hostProvider, sessionTimeout, zooKeeper, watcher,
                clientCnxnSocket, 0, new byte[16], canBeReadOnly);
    }

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


    private final static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.error("from" + t.getName(), e);
        }
    };






    public void start(){
        sendThread.start();
        eventThread.start();
    }


    private static class WatcherSetEventPair {
        private final Set<Watcher> watchers;
        private final WatchedEvent event;

        public WatcherSetEventPair(Set<Watcher> watchers, WatchedEvent event) {
            this.watchers = watchers;
            this.event = event;
        }
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


    /**
     * This class services the outgoing request queue and generates the heart
     * beats. It also spawns the ReadThread
     */
    class SendThread extends ZooKeeperThread {
        private long lastPingSentNs;
        private final ClientCnxnSocket clientCnxnSocket;
        private Random r = new Random(System.nanoTime());
        private boolean isFirstConnect = true;

        ClientCnxnSocket getClientCnxnSocket(){
            return clientCnxnSocket;
        }

        public SendThread(ClientCnxnSocket clientCnxnSocket) {
            super(makeThreadName("-SendThread()"));
            state = ZooKeeper.States.CONNECTING;
            this.clientCnxnSocket = clientCnxnSocket;
            setUncaughtExceptionHandler(uncaughtExceptionHandler);
            setDaemon(true);
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
    }

    static class AuthData {
        AuthData(String scheme, byte data[]){
            this.scheme = scheme;
            this.data = data;
        }

        String scheme;
        byte data[];
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
}
