package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Environment;
import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.jmx.MBeanRegistry;
import com.lami.tuomatuo.mq.zookeeper.server.auth.AuthenticationProvider;
import com.lami.tuomatuo.mq.zookeeper.server.auth.ProviderRegistry;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.ReadOnlyZooKeeperServer;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.proto.*;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * This class implements a simple standalone ZooKeeperServer. It sets up the
 * following chain of RequestProcessors to process request:
 * PrepRequestProcessor -> SyncRequestProcessor -> FinalRequestProcessor
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServer implements SessionTracker.SessionExpirer, ServerStats.Provider {

    protected static final Logger LOG;

    static {
        LOG = LoggerFactory.getLogger(ZooKeeperServer.class);
        Environment.logEnv("Server environment:" , LOG);
    }

    protected ZooKeeperServerBean jmxServerBean;
    protected DataTreeBean jmxDataTreeBean;


    /**
     * The server delegates loading of the tree to an instance of the interface
     */
    public interface DataTreeBuilder{
        public DataTree build();
    }

    static public class BasicDataTreeBuilder implements DataTreeBuilder{
        public DataTree build(){
            return new DataTree();
        }
    }

    public static final int DEFAULT_TICK_TIME = 3000;
    protected int tickTime = DEFAULT_TICK_TIME;
    /** value of -1 indicates unset, use default */
    protected int minSessionTimeout = -1;
    /** value of -1 indicates unset, use default */
    protected int maxSessionTimeout = -1;

    protected SessionTracker sessionTracker;
    private FileTxnSnapLog txnLogFactory = null;
    private ZKDatabase zkDb;
    protected long hzxid = 0;
    public final static Exception ok = new Exception("No prob");
    protected RequestProcessor firstProcessor;
    protected volatile boolean running;

    /**
     * This is the secret that we use to to generate passwords, for the moment it
     * is more of a sanity check
     */
    static final private long superSecret = 0XB3415C00L;
    public int requestsInProcess;
    public final List<ChangeRecord> outstandingChanges = new ArrayList<ChangeRecord>();
    public final HashMap<String, ChangeRecord> outstandingChangesForPath =
            new HashMap<String, ChangeRecord>();

    private ServerCnxnFactory serverCnxnFactory;

    private  ServerStats serverStats;

    void removeCnxn(ServerCnxn cnxn){
        zkDb.removeCnxn(cnxn);
    }

    /**
     * Creates a ZooKeeperServer instance Nothing is setup, use the SetX
     * methods to prepare the instance (eg datadir datalogdir, tickTime)
     */
    public ZooKeeperServer() {
        serverStats = new ServerStats(this);
    }

    public ZooKeeperServer(FileTxnSnapLog txnLogFactory, int tickTime,
                           int minSessionTimeout, int maxSessionTimeout,
                           DataTreeBuilder treeBuilder, ZKDatabase zkDb) {
        serverStats = new ServerStats(this);
        this.txnLogFactory = txnLogFactory;
        this.zkDb = zkDb;
        this.tickTime = tickTime;
        this.minSessionTimeout = minSessionTimeout;
        this.maxSessionTimeout = maxSessionTimeout;

        LOG.info("Created server with tickTime " + tickTime
                        + " minSessionTimeout " + getMinSessionTimeout()
                        + " maxSessionTimeout " + getMaxSessionTimeout()
                        + " datadir " + txnLogFactory.getDataDir()
                        + " snapdir " + txnLogFactory.getSnapDir()
        );
    }

    /**
     * Creates a zookeeperserver instance
     */
    public ZooKeeperServer(FileTxnSnapLog txnLogFactory, int tickTime,
                           DataTreeBuilder treeBuilder) throws Exception{
        this(txnLogFactory, tickTime, -1, -1, treeBuilder, new ZKDatabase(txnLogFactory));
    }

    public ServerStats serverStats(){
        return serverStats;
    }

    public void dumpConf(PrintWriter pwriter){
        pwriter.print("clientPort=");
        pwriter.print(getClientPort());
        pwriter.print("dataDir=");
        pwriter.print(zkDb.snapLog.getSnapDir().getAbsoluteFile());
        pwriter.print("dataLogDir=");
        pwriter.print(zkDb.snapLog.getDataDir().getAbsoluteFile());
        pwriter.print("tickTime=");
        pwriter.print(getTickTime());
        pwriter.print("maxClientCnxn=");
        pwriter.print(serverCnxnFactory.getMaxClientCnxnsPerHost());
        pwriter.print("minSessionTimeout = ");
        pwriter.print(getMinSessionTimeout());
        pwriter.print("maxSessionTimeout=");

        pwriter.print(getMaxSessionTimeout());

        pwriter.print("serverId=");
        pwriter.print(getServerId());
    }

    // This constructor is for backward compatibility with the existing unit
    // test code
    public ZooKeeperServer(File snapDir, File logDir, int tickTime) throws Exception{
        this(new FileTxnSnapLog(snapDir, logDir), tickTime, new BasicDataTreeBuilder());
    }

    // Default constructor, relies on the config for its argument values
    public ZooKeeperServer(FileTxnSnapLog txnLogFactory, DataTreeBuilder treeBuilder) throws Exception{
        this(txnLogFactory, DEFAULT_TICK_TIME, -1, -1, treeBuilder, new ZKDatabase(txnLogFactory));
    }

    // set the zookeeper database for this server
    public ZKDatabase getZkDatabase(){
        return this.zkDb;
    }

    // ste the zkdatabase for this zookeeper server
    public void setZKDatabase(ZKDatabase zkDb){
        this.zkDb = zkDb;
    }

    public void loadData() throws Exception{
        /**
         * When a new leader starts executing Leader#lead, it
         * invokes this method. The database, however, has been
         * initialized before running leader election so that
         * the server could pick its zxid for its initial vote
         * It does it by invoking QuorumPeer#getLastLoggedZxid
         * Consuquently, we don't need to initialize it once more
         * and avoid the penalty of loading it a second time. Not
         * reloading it is particularly important for applications
         * that host a large database
         *
         * The following if block checks whether the database has
         * been initialized or not. Not that this method is
         * invoked by at least one other method
         * ZooKeeperServer#startData
         */

        if(zkDb.isInitialized()){
            setZxid(zkDb.getDataTreeLastProcessedZxid());
        }else{
            setZxid(zkDb.loadDataBase());
        }

        // Clean up dead sessions
        LinkedList<Long> deadSessions = new LinkedList<>();
        for(Long session : zkDb.getSessions()){
            if(zkDb.getSessionsWithTimeouts().get(session) == null){
                deadSessions.add(session);
            }
        }

        zkDb.setDataTreeInit(true);
        for(long session : deadSessions){
            killSession(session, zkDb.getDataTreeLastProcessedZxid());
        }
    }

    public void takeSnapshot(){
        try{
            txnLogFactory.save(zkDb.getDataTree(), zkDb.getSessionsWithTimeouts());
        }catch (Exception e){
            LOG.info("Server unrecoverable error, exiting", e);
            System.exit(0);
        }
    }

    // This should be called from a synchronized block on this
    synchronized public long getZxid(){
        return hzxid;
    }

    synchronized public long getNextZxid(){
        return ++hzxid;
    }

    synchronized public void setZxid(long zxid){
        hzxid = zxid;
    }

    long getTime(){
        return System.currentTimeMillis();
    }

    public void close(long sessionId){
        submitRequest(null, sessionId, ZooDefs.OpCode.closeSession, 0, null, null);
    }

    public void closeSession(long sessionId){
        LOG.info("Closing session 0X:" + Long.toHexString(sessionId));
        // we do not want to wait for a session close, send it as soon as we
        // detect it
        clone(sessionId);
    }

    public void killSession(long sessionId, long zxid){
        zkDb.killSession(sessionId, zxid);

        if(sessionTracker != null){
            sessionTracker.removeSession(sessionId);
        }
    }


    public void expire(SessionTracker.Session session){
        long sessionId = session.getSessionId();
        close(sessionId);
    }

    public static class MissingSessionException extends IOException{
        private static final long serialVersionUID = 7467414635467261007L;

        public MissingSessionException(String msg) {
            super(msg);
        }
    }

    void touch(ServerCnxn cnxn) throws Exception{
        if(cnxn == null){
            return;
        }
        long id = cnxn.getSessionId();
        int to = cnxn.getSessionTimeout();
        if(!sessionTracker.touchSession(id, to)){
            throw new MissingSessionException("No session with sessionid 0x" +
            Long.toHexString(id)
            + " exists, probably expired and rmoved");
        }
    }

    protected void registerJMX(){
        try{
            jmxServerBean = new ZooKeeperServerBean(this);
            MBeanRegistry.getInstance().register(jmxServerBean, null);

            try{
                jmxDataTreeBean = new DataTreeBean(zkDb.getDataTree());
                MBeanRegistry.getInstance().register(jmxDataTreeBean, jmxServerBean);
            }catch (Exception e){
                LOG.info("Failed to register with JMX");
                jmxDataTreeBean = null;
            }

        }catch (Exception e){
            LOG.warn("Failed to register with JMX");
            jmxServerBean = null;
        }
    }

    public void startdata()throws Exception{
        // check to see if zkDb is not null
        if(zkDb == null){
            zkDb = new ZKDatabase(this.txnLogFactory);
        }
        if(!zkDb.isInitialized()){
            loadData();
        }
    }

    public void startup(){
       if(sessionTracker == null){
           createSessionTracker();
       }

        startSessionTracker();
        setupRequestProcessors();

        synchronized (this){
            running = true;
            notifyAll();
        }
    }

    protected void setupRequestProcessors(){
        RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        RequestProcessor syncProcessor = new SyncRequestProcessor(this, finalProcessor);
        ((SyncRequestProcessor)syncProcessor).start();
        firstProcessor = new PrepRequestProcessor(this, syncProcessor);
        ((PrepRequestProcessor)firstProcessor).start();
    }


    protected void createSessionTracker(){
        sessionTracker = new SessionTrackerImpl(this, zkDb.getSessionsWithTimeouts(), tickTime, 1);
    }

    public void startSessionTracker(){
        ((SessionTrackerImpl)sessionTracker).start();
    }

    public boolean isRunning(){
        return running;
    }

    // zookeeper 服务端关闭
    public void shutdown(){
        LOG.info("shutting down");

        // new RuntimeException("Calling shutdown").printStackTrace()
        this.running = false;
        /**
         * Since sessionTracker and syncThreads poll we just have to
         * set running to false and they will detect it during the poll
         * interval
         */

        if(sessionTracker != null){
            sessionTracker.shutdown();
        }
        if(firstProcessor != null){
            firstProcessor.shutdown();
        }

        if(zkDb != null){
            zkDb.clear();
        }

        unregisterJMX();
    }


    protected void unregisterJMX(){
        try{
            if(jmxDataTreeBean != null){
                MBeanRegistry.getInstance().unregister(jmxDataTreeBean);
            }
        }catch (Exception e){
            LOG.info("Failed to unregister with JMX", e);
        }

        try{
            if(jmxServerBean != null){
                MBeanRegistry.getInstance().unregister(jmxServerBean);
            }
        }catch (Exception e){
            LOG.warn("Failed to unregister with JMX", e);
        }
        jmxServerBean = null;
        jmxDataTreeBean = null;
    }

    synchronized public void incInProcess(){
        requestsInProcess++;
    }

    synchronized public void decInProcess(){
        requestsInProcess--;
    }

    public int getInProcess(){
        return requestsInProcess;
    }

    static class ChangeRecord{

        long zxid;

        String path;

        StatPersisted stat; // make sure to create a new object when  changing

        int childCount;

        List<ACL> acl; // make sure to create a new object when changing

        public ChangeRecord(long zxid, String path, StatPersisted stat,
                            int childCount, List<ACL> acl) {
            this.zxid = zxid;
            this.path = path;
            this.stat = stat;
            this.childCount = childCount;
            this.acl = acl;
        }


        ChangeRecord duplicate(long zxid){
            StatPersisted stat = new StatPersisted();
            if(this.stat != null){
                DataTree.copyStatPersisted(this.stat, stat);
            }
            return new ChangeRecord(zxid, path, stat, childCount,
                    acl == null? new ArrayList<ACL>() : new ArrayList<>(acl));
        }

    }

    byte[] generatePasswd(long id){
        Random r = new Random(id ^ superSecret);
        byte p[] = new byte[16];
        r.nextBytes(p);
        return p;
    }

    protected boolean checkPasswd(long sessionId, byte[] passwd){
        return sessionId != 0
                && Arrays.equals(passwd, generatePasswd(sessionId));
    }

    long createSession(ServerCnxn cnxn, byte passwd[], int timeout){
        long sessionId = sessionTracker.createSession(timeout);
        Random r = new Random(sessionId ^ superSecret);
        r.nextBytes(passwd);
        ByteBuffer to = ByteBuffer.allocate(4);
        to.putInt(timeout);
        cnxn.setSessionId(sessionId);
        submitRequest(cnxn, sessionId, ZooDefs.OpCode.createSession, 0, to, null);
        return sessionId;
    }

    public void setOwner(long id, Object owner) throws Exception{
        sessionTracker.setOwner(id, owner);
    }

    public void revalidateSession(ServerCnxn cnxn, long sessionId,
                                  int sessionTimeout)throws Exception{
        boolean rc = sessionTracker.touchSession(sessionId, sessionTimeout);
        finishSessionInit(cnxn, rc);
    }

    public void reopenSession(ServerCnxn cnxn, long sessionId, byte[] passwd, int sessionTimeout) throws Exception{
        if(!checkPasswd(sessionId, passwd)){
            finishSessionInit(cnxn, false);
        }else{
            revalidateSession(cnxn, sessionId, sessionTimeout);
        }
    }

    public void finishSessionInit(ServerCnxn cnxn, boolean valid){
        // register with JMX
        try{
            if(valid){
                serverCnxnFactory.registerConnection(cnxn);
            }
        }catch (Exception e){
            LOG.info("Failed to register with JMX", e);
        }

        try{
            ConnectResponse rsp = new ConnectResponse(0, valid? cnxn.getSessionTimeout(): 0, valid?cnxn.getSessionId():0, valid?generatePasswd(cnxn.getSessionId()) : new byte[16]);
            ByteArrayOutputStream
        }catch (Exception e){
            LOG.info("Exception while establishing session, closing", e);
            cnxn.close();
        }
    }










    @Override
    public long getDataDirSize() {
        return 0;
    }

    @Override
    public long getLogDirSize() {
        return 0;
    }

    @Override
    public long getServerId() {
        return 0;
    }

    public enum State {
        INITIAL, RUNNING, SHUTDOWN, ERROR;
    }








    /**
     * get the zookeeper database for this server
     */
    public ZKDatabase getZKDatabase(){
        return this.zkDb;
    }



    public void submitRequest(Request si){

    }

    public static int getSnapCount(){
        String sc = System.getProperty("zookeeper.snapCount");
        try{
            int snapCount = Integer.parseInt(sc);
            // snapCount must be 2 or more
            if(snapCount < 2){
                LOG.info("SnapCount should be 2 or more. Now snapCount is reset to 2");
                snapCount = 2;
            }
        }catch (Exception e){
            return 100000;
        }
    }

    public int getGlobalOutstandingLimit(){
        String sc= System.getProperty("zookeeper.globalOutstandingLimit");
        int limit;
        try{
            limit = Integer.parseInt(sc);
        }catch (Exception e){
            limit = 1000;
        }
        return limit;
    }

    public void setServerCnxnFactory(ServerCnxnFactory factory){
        serverCnxnFactory = factory;
    }

    public ServerCnxnFactory getServerCnxnFactory(){
        return serverCnxnFactory;
    }

    // return the last processed id from the
    // datatree
    public long getLastProcessedZxid(){
        return zkDb.getDataTreeLastProcessedZxid();
    }

    /**
     * return the outstanding requests
     * in the queue, which haven't been
     * processed yet
     */
    public long getOutstandingRequests(){
        return getInProcess();
    }

    /**
     * truncate the log to get in sync with others
     * if in a quorum
     */
    public void truncateLog(long zxid) throws Exception{
        this.zkDb.truncateLog(zxid);
    }

    public int getTickTime(){
        return tickTime;
    }

    public void setTickTime(int tickTime){
        LOG.info("tickTime set to " + tickTime);
        this.tickTime = tickTime;
    }

    public int getMinSessionTimeout(){
        return minSessionTimeout == -1 ? tickTime * 2 : minSessionTimeout;
    }

    public void setMinSessionTimeout(int min){
        LOG.info("minSessionTimeout set to " + min);
        this.minSessionTimeout = min;
    }

    public int getMaxSessionTimeout(){
        return maxSessionTimeout == -1 ? tickTime * 20 : maxSessionTimeout;
    }

    public void setMaxSessionTimeout(int max){
        LOG.info("maxSessionTimeout set to " + max);
        this.maxSessionTimeout = max;
    }

    public int getClientPort(){
        return serverCnxnFactory != null ? serverCnxnFactory.getLocalPort() : -1;
    }

    public void setTxnLogFactory(FileTxnSnapLog txnLog){
        this.txnLogFactory = txnLog;
    }

    public FileTxnSnapLog getTxnLogFactory(){
        return this.txnLogFactory;
    }

    public String getState(){
        return "standalone";
    }

    public void dumpEphemerals(PrintWriter pwriter){
        zkDb.dumpEphemerals(pwriter);
    }

    /**
     * return the total number of client connections that are alive
     * to this server
     */
    public int getNumAliveConnections(){
        return serverCnxnFactory.getNumAliveConnections();
    }

    public void processConnectRequest(ServerCnxn cnxn, ByteBuffer incomingBuffer) throws Exception{
        BinaryInputArchive bia = BinaryInputArchive.getArchive(new ByteBufferInputStream(incomingBuffer));
        ConnectRequest connReq = new ConnectRequest();
        connReq.deserialize(bia, "connect");
        LOG.info("Session establishment request from client"
                    + cnxn.getRemoteSocketAddress()
                    + " client's lastZxid is 0x"
                    + Long.toHexString(connReq.getLastZxidSeen()));
        boolean readOnly = false;
        try{
            readOnly = bia.readBool("readOnly");
            cnxn.isOldClient = false;
        }catch (Exception e){
            LOG.info("Connection request from old client "
                        + cnxn.getRemoteSocketAddress()
                        + ", will be dropped if server is in r-o mode");

        }

        if(readOnly == false && this instanceof ReadOnlyZooKeeperServer){
            String msg = "Refusing session request for not-ready-only client "
                    + cnxn.getRemoteSocketAddress();
            LOG.info(msg);
            throw new ServerCnxn.CloseRequestException(msg);
        }

        if(connReq.getLastZxidSeen() > zkDb.dataTree.lastProcessedZxid){
            String msg = "Refusing session request for client "
                            + cnxn.getRemoteSocketAddress()
                    + " as it has seen zxid 0x"
                    + Long.toHexString(connReq.getLastZxidSeen())
                    + " our last zxid is 0x"
                    + Long.toHexString(getZkDatabase().getDataTreeLastProcessedZxid())
                    + " client must try another server";
            LOG.info(msg);
            throw new ServerCnxn.CloseRequestException(msg);
        }

        int sessionTimeout = connReq.getTimeOut();
        byte passwd[] = connReq.getPasswd();
        int minSessionTimeout = getMinSessionTimeout();
        if(sessionTimeout < minSessionTimeout){
            sessionTimeout = minSessionTimeout;
        }
        int maxSessionTimeout = getMaxSessionTimeout();
        if(sessionTimeout > maxSessionTimeout){
            sessionTimeout = maxSessionTimeout;
        }
        cnxn.setSessionTimeout(sessionTimeout);
        /**
         * We don't want to receive any packets until we are sure that the
         * session is setup
         */
        cnxn.disableRecv();
        long sessionId = connReq.getSessionId();
        if(sessionId != 0){
            long clientSessionId = connReq.getSessionId();
            LOG.info("Client attempting to renew session 0x"
                    + Long.toHexString(clientSessionId)
                    + " at " + cnxn.getRemoteSocketAddress());
            serverCnxnFactory.closeSession(sessionId);
            cnxn.setSessionId(sessionId);
            reopenSession(cnxn, sessionId, passwd, sessionTimeout);
        }else{
            LOG.info("Client attempting to establish new session at "
                + cnxn.getRemoteSocketAddress());
            createSession(cnxn, passwd, sessionTimeout);
        }
    }

    public boolean shouldThrottle(long outStandingCount){
        if(getGlobalOutstandingLimit() < getInProcess()){
            return outStandingCount > 0;
        }
        return false;
    }

    public void processPacket(ServerCnxn cnxn, ByteBuffer incomingBuffer)throws Exception{
        // we have the request, now process and setup for next
        InputStream bais = new ByteBufferInputStream(incomingBuffer);
        BinaryInputArchive bia = BinaryInputArchive.getArchive(bais);
        RequestHeader h = new RequestHeader();
        h.deserialize(bia, "header");
        /**
         * Through the magic of byte buffers, txn will not be
         * pointing
         * to the start of the txn
         */
        incomingBuffer = incomingBuffer.slice();
        if(h.getType() == ZooDefs.OpCode.auth){
            LOG.info("got auth packet " + cnxn.getRemoteSocketAddress());
            AuthPacket authPacket = new AuthPacket();
            ByteBufferInputStream.byteBuffer2Record(incomingBuffer, authPacket);
            String scheme = authPacket.getScheme();
            AuthenticationProvider ap = ProviderRegistry.getProvider(scheme);
            KeeperException.Code authReturn = KeeperException.Code.AUTHFAILED;
            if(ap != null){
                try{
                    authReturn = ap.handleAuthentication(cnxn, authPacket.getAuth());
                }catch (Exception e){
                    LOG.info("Caught runtime exception from AuthenticationProvider: " + scheme + " due to " + e);
                    authReturn = KeeperException.Code.AUTHFAILED;
                }
            }

            if(authReturn != KeeperException.Code.OK){
                LOG.info("No authentication provider for scheme: " +
                scheme + " has "
                + ProviderRegistry.listProviders());
                LOG.info("Authentication failed for scheme : " + scheme);
                ReplyHeader rh = new ReplyHeader(h.getXid(), 0,
                        KeeperException.Code.AUTHFAILED.intValue());
                cnxn.sendResponse(rh, null, null);
                cnxn.sendBuffer(ServerCnxnFactory.closeConn);
                cnxn.disableRecv();
            }else{
                LOG.info("Authentication successed for scheme: " + scheme);
                LOG.info("auth success " + cnxn.getRemoteSocketAddress());
                ReplyHeader rh = new ReplyHeader(h.getXid(), 0,
                        KeeperException.Code.OK.intValue());
                cnxn.sendResponse(rh, null, null);
            }

            return;
        }
        else{
            if(h.getType() == ZooDefs.OpCode.sasl){
                Record rsp = processSasl(incomingBuffer, cnxn);
                ReplyHeader rh = new ReplyHeader(h.getXid(), 0, KeeperException.Code.OK.intValue());
                cnxn.sendResponse(rh, rsp, "response"); // not sure about 3rh arg..what is it
            }else{
                Request si = new Request(cnxn, cnxn.getSessionId(), h.getXid(),
                        h.getType(), incomingBuffer, cnxn.getAuthInfo());
                si.setOwner(ServerCnxn.me);
                submitRequest(si);
            }
        }

        cnxn.incrOutstandingRequests(h);
    }

    public Record processSasl(ByteBuffer incomingBuffer, ServerCnxn cnxn) throws Exception{
        LOG.info("Responding to client SASL token");
        GetSASLRequest clientTokenRecord = new GetSASLRequest();
        ByteBufferInputStream.byteBuffer2Record(incomingBuffer, clientTokenRecord);
        byte[] clientToken = clientTokenRecord.getToken();
        LOG.info("Size of client SASL token:" + clientToken.length);
        byte[] responseToen = null;
        try{
            ZooKeeperSaslServer saslServer = cnxn.zooKeeperSaslServer;
            try{
                /**
                 * note that clientToken might be empty (clientToen.length == 0)
                 * if using the DIGEST-MD5 mechanism, clientToen will be empty at the begining of the
                 * SASL negotation process
                 */
                responseToen = saslServer.evaluateResponse(clientToken);
                if(saslServer.isComplete() == true){
                    String authorizationID = saslServer.getAuthorizationID();
                    LOG.info("adding SASL authenticate for authenticationID : " + authorizationID);
                    cnxn.addAuthInfo(new Id("sasl", authorizationID));
                }

            }catch (Exception e){
                LOG.info("Client failed to SASL authenticate: " + e);
                if((System.getProperty("zookeeper.allowSaslFailedClients") != null)
                        && (System.getProperty("zookeeper.allowSaslFailedClients").equals("true"))){
                    LOG.info("Maintaining client connection despite SASL authenticate failure");
                }else{
                    LOG.warn("Closing client connection due to SASL authenticate failure");
                    cnxn.close();
                }
            }
        }catch (NullPointerException e){
            LOG.info("cnxn.saslServer is null: cnxn object did not initialize its saslServer properly");
        }
        if(responseToen != null){
            LOG.info("Size of server SASL response : " + responseToen.length);
        }
        // wrap SASL response token to client inside a response object
        return new SetSASLResponse(responseToen);
    }


    public DataTree.ProcessTxnResult processTxn(TxnHeader hdr, Record txn){
        DataTree.ProcessTxnResult rc;
        int opCode = hdr.getType();
        long sessionId = hdr.getClientId();
        rc = getZKDatabase().processTxn(hdr, txn);
        if(opCode == ZooDefs.OpCode.createSession){
            if(txn instanceof CreateSessionTxn){
                CreateSessionTxn cst = (CreateSessionTxn)txn;
                sessionTracker.addSession(sessionId, cst.getTimeOut());
            }else{
                LOG.warn("Got" + txn.getClass() + " " + txn.toString());
            }
        }else if(opCode == ZooDefs.OpCode.closeSession){
            sessionTracker.removeSession(sessionId);
        }
        return rc;
    }
}
