package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.common.AtomicFileOutputStream;
import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxnFactory;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumMaj;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumVerifier;
import com.lami.tuomatuo.mq.zookeeper.server.util.ZxidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * The class manages the quorum protocol. There are three states this server
 * can be in
 * Leader election - each server will elect a leader (proposing itself as a
 * leader initially)
 * Follower - the server will synchronize with the leader and replicate any
 * transaction
 * Leader - the server will process requests and forward them to followers
 * A majority of follower must log the request before it can be accepted
 *
 * This class will setup a datagram socket that will always respond with its
 * view of the current leader. The response will take the form of
 *
 * int xid;
 * long myid;
 * long leader_id
 * long leader_zxid
 *
 *
 * The request for the current leader will consist solely of an xid; int xid
 *
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumPeer extends Thread implements QuorumStats.Provider{

    private static final Logger LOG = LoggerFactory.getLogger(QuorumPeer.class);

    public QuorumBean jmxQuorumBean;
    public LocalPeerBean jmxLocakPeerbean;
    public LeaderElectionBean jmxLeaderElectionBean;
    public QuorumCnxManager qcm;

    /**
     * ZKDatabase is a top level member of quorum peer
     * which will be used in all the zookeeperservers
     * instantiated later. Also. it is created once on
     * bootup and only thrown away in case of a truncate
     * message from the leader
     */
    private ZKDatabase zkDb;

    public static class QuorumServer{

        public QuorumServer(long id, InetSocketAddress addr, InetSocketAddress electionAddr) {
            this.addr = addr;
            this.electionAddr = electionAddr;
            this.id = id;
        }

        public QuorumServer(long id, InetSocketAddress addr) {
            this.addr = addr;
            this.id = id;
        }

        public QuorumServer(long id, InetSocketAddress addr, InetSocketAddress electionAddr,  LearnerType type) {
            this.addr = addr;
            this.electionAddr = electionAddr;
            this.id = id;
            this.type = type;
        }

        public InetSocketAddress addr;
        public InetSocketAddress electionAddr;
        public long id;

        public LearnerType type = LearnerType.PARRTICIPANT;
    }

    public enum ServerState {
        LOOKING, FOLLOWING, LEADING, OBSERVING;
    }

    /**
     * A peer can either be participating, which impies that it is willing to
     * both vote in instance of consensus and to elect or become a Leader, or
     * it may be observing in which case it isn't
     *
     * We need this distinction to decide which ServerState to move to when
     * conditions change
     */
    public enum LearnerType{
        PARRTICIPANT, OBSERVER;
    }

    /**
     * To enable observers to have no identifier, we need a generic identifier
     * at least for QuorumCnxManager, We use the following constant to as the
     * value of such a generic identifier
     */
    static final long OBSERVER_ID = Long.MAX_VALUE;

    /** Record leader election time */
    public long start_fle, end_fle;

    public LearnerType learnerType = LearnerType.PARRTICIPANT;

    public LearnerType getLearnerType(){
        return learnerType;
    }

    // Sets the LearnerType both in the QuorumPeer and in the peerMap
    public void setLearnerType(LearnerType p){
        learnerType = p;
        if(quorumPeers.containsKey(this.myid)){
            this.quorumPeers.get(myid).type = p;
        }else{
            LOG.info("Setting LearnerType to " + p + " but " + myid
                    + " not in QuorumPeers. ");
        }
    }
    // The server that make up the cluster
    protected Map<Long, QuorumServer> quorumPeers;
    public int getQuorumSize(){
        return getVotingView().size;
    }

    // QuorumVerifier implementation
    private QuorumVerifier quorumConfig;

    // My id
    private long myid;

    // get the id of this quorum peer
    public long getId(){
        return myid;
    }
    // This is who I think the leader currently is
    volatile private Vote currentVote;

    // and its counterpart for backward compatibility
    volatile private Vote bcVote;

    public synchronized Vote getCurrentVote(){
        return currentVote;
    }

    public synchronized void setCurrentVote(Vote v){
        currentVote = v;
    }

    synchronized Vote getBCVote(){
        if(bcVote == null){
            return currentVote;
        }
        else{
            return bcVote;
        }
    }

    synchronized void setBCVote(Vote v){
        bcVote = v;
    }

    volatile  boolean running = true;

    // The number of milliseconds of each tick
    protected int tickTime;

    /**
     * Minimum number of milliseconds to allow for session timeout
     * A value of -2 indicates unset, use default
     */
    protected int minSessionTimeout = -1;

    /**
     * Maximum number of millisesonds to allow for session timeout
     * A value of -1 indicates unset use default
     */
    protected int maxSessionTimeout = -1;

    /**
     * The number of ticks that the initial synchronization phase can take
     */
    protected int initLimit;

    /**
     * The number of ticks that can pass between sending a request and getting
     * an acknowledgment
     */
    protected int syncLimit;

    /**
     * Enable/Disable sync request processor. This option is enabled
     * by default and is to be used with observers
     */
    public boolean syncEnabled = true;

    // The current tick
    protected volatile int tick;

    /**
     * Whether or not to listen on all IPs for two quorum ports
     * (broadcast and fast leader election)
     */
    protected boolean quorumListenOnAllIPs = false;


    /**
     * @deprecated As of release 3.4.0, this class has been deprecated, since
     * it is used with one of the udp-based versions of leader election, which
     * we are also deprecating.
     *
     * This class simply responds to requests for the current leader of this
     * node.
     * <p>
     * The request contains just an xid generated by the requestor.
     * <p>
     * The response has the xid, the id of this server, the id of the leader,
     * and the zxid of the leader.
     *
     *
     */
    @Deprecated
    class ResponderThread extends Thread {
        ResponderThread() {
            super("ResponderThread");
        }

        volatile boolean running = true;

        @Override
        public void run() {
            try {
                byte b[] = new byte[36];
                ByteBuffer responseBuffer = ByteBuffer.wrap(b);
                DatagramPacket packet = new DatagramPacket(b, b.length);
                while (running) {
                    udpSocket.receive(packet);
                    if (packet.getLength() != 4) {
                        LOG.warn("Got more than just an xid! Len = "
                                + packet.getLength());
                    } else {
                        responseBuffer.clear();
                        responseBuffer.getInt(); // Skip the xid
                        responseBuffer.putLong(myid);
                        Vote current = getCurrentVote();
                        switch (getPeerState()) {
                            case LOOKING:
                                responseBuffer.putLong(current.getId());
                                responseBuffer.putLong(current.getZxid());
                                break;
                            case LEADING:
                                responseBuffer.putLong(myid);
                                try {
                                    long proposed;
                                    synchronized(leader) {
                                        proposed = leader.lastProposed;
                                    }
                                    responseBuffer.putLong(proposed);
                                } catch (NullPointerException npe) {
                                    // This can happen in state transitions,
                                    // just ignore the request
                                }
                                break;
                            case FOLLOWING:
                                responseBuffer.putLong(current.getId());
                                try {
                                    responseBuffer.putLong(follower.getZxid());
                                } catch (NullPointerException npe) {
                                    // This can happen in state transitions,
                                    // just ignore the request
                                }
                                break;
                            case OBSERVING:
                                // Do nothing, Observers keep themselves to
                                // themselves.
                                break;
                        }
                        packet.setData(b);
                        udpSocket.send(packet);
                    }
                    packet.setLength(b.length);
                }
            } catch (RuntimeException e) {
                LOG.warn("Unexpected runtime exception in ResponderThread",e);
            } catch (IOException e) {
                LOG.warn("Unexpected IO exception in ResponderThread",e);
            } finally {
                LOG.warn("QuorumPeer responder thread exited");
            }
        }
    }


    public ServerState state = ServerState.LOOKING;

    public synchronized void setPeerState(ServerState newState){
        state = newState;
    }

    public synchronized ServerState getPeerState(){
        return state;
    }

    DatagramSocket udpSocket;

    private InetSocketAddress myQuorumAddr;

    public InetSocketAddress getQuorumAddress(){
        return myQuorumAddr;
    }

    public int electionType;


    public Election electionAlg;

    public ServerCnxnFactory cnxnFactory;

    private FileTxnSnapLog logFactory = null;

    public QuorumStats quorumStats;

    public QuorumPeer(){
        super("QuorumPeer");
        quorumStats = new QuorumStats(this);
    }

    public QuorumPeer(Map<Long, QuorumServer> quorumPeers, File dataDir,
                      File dataLogDir, int electionType,
                      long myid, int tickTime, int initLimit, int syncLimit,
                      ServerCnxnFactory cnxnFactory)throws IOException{
        this(quorumPeers, dataDir, dataLogDir, electionType, myid, tickTime,
                initLimit, syncLimit, false, cnxnFactory,
                new QuorumMaj(countParticipants(quorumPeers)));
    }

    public QuorumPeer(Map<Long, QuorumServer> quorumPeers, File dataDir,
                      File dataLogDir, int electionType,
                      long myid, int tickTime, int initLimit, int syncLimit,
                      boolean quorumListenOnAllIPs,
                      ServerCnxnFactory cnxnFactory,
                      QuorumVerifier quorumConfig)throws IOException{
        this();
        this.cnxnFactory = cnxnFactory;
        this.quorumPeers = quorumPeers;
        this.electionType = electionType;
        this.myid = myid;
        this.tickTime = tickTime;
        this.initLimit = initLimit;
        this.syncLimit = syncLimit;
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
        this.logFactory = new FileTxnSnapLog(dataLogDir, dataDir);
        this.zkDb = new ZKDatabase(this.logFactory);
        if(quorumConfig == null){
            this.quorumConfig = new QuorumMaj(countParticipants(quorumPeers));
        }
        else{
            this.quorumConfig = quorumConfig;
        }
    }

    public QuorumStats quorumStats(){
        return quorumStats;
    }

    @Override
    public synchronized void start() {
        loadDataBase();
        cnxnFactory.start;
        startLeaderElection();
        super.start();
    }


    public void loadDataBase(){
        File updating = new File(getTxnFactory().getSnapDir(),
                UPDATING_EPOCH_FILENAME);
        try{
            zkDb.loadDataBase();

            // load the epochs
            long lastProcessedZxid = zkDb.getDataTree().lastProcessedZxid;
            long epochOfZxid = ZxidUtils.getEpochFromZxid(lastProcessedZxid);
            try{

            }catch (Exception e){
                currentEpoch = epochOfZxid;

            }
        }catch (IOException e){
            throw new RuntimeException("Unable to run quorum server", e);
        }
    }

    public QuorumPeer(String name) {
        super(name);
    }


    public Follower follower;
    public Leader leader;
    public Observer observer;




    synchronized public ZooKeeperServer getActiveServer(){
        return null;
    }

    @Override
    public void run() {
    }

    public void shutdown(){

    }

    public Map<Long, QuorumPeer.QuorumServer> getView(){
        return Collections.unmodifiableMap(this.quorumPeers);
    }

    public Map<Long, QuorumPeer.QuorumServer> getVotingView(){
        Map<Long, QuorumPeer.QuorumServer> ret = new HashMap<>();
        Map<Long, QuorumPeer.QuorumServer> view = getView();
        for(QuorumObserver quorumObserver : view.values()){
            if(server.type == LearnerType.PARRTICIPANT){
                ret.put(server.id, quorumObserver);
            }
        }

        return ret;
    }

    public Map<Long, QuorumPeer.QuorumServer> getObservingView(){
        Map<Long, QuorumPeer.QuorumServer> ret = new HashMap<>();
        Map<Long, QuorumPeer.QuorumServer> view = getView();
        for(QuorumObserver quorumObserver : view.values()){
            if(server.type == LearnerType.OBSERVER){
                ret.put(server.id, quorumObserver);
            }
        }

        return ret;
    }

    /**
     * Check if a node is in the current view. With static membership, the
     * result of the check will never change; only when dynamic membership
     * is introduced will this be more useful
     */
    public boolean viewContains(Long sid){
        return this.quorumPeers.containsKey(sid);
    }

    // Only used by QuorumStats at the moment
    public String[] getQuorumPeers(){
        List<String> l = new ArrayList<>();
        synchronized (this){
            if(leader != null){
                for(LearnerHandler fh : leader.getLearners()){
                    if(fh.getSocket() != null){
                        String s = fh.getSocket().getRemoteSocketAddress().toString();
                        if(leader.isLearnerSynced(fh)){
                            s += "*";
                        }
                        l.add(s);
                    }
                }
            }else if(follower != null){
                l.add(follower.sock.getRemoteSocketAddress().toString());
            }
        }

        return l.toArray(new String[0]);
    }



    public String getServerState(){
        switch (getPeerState()){
            case LOOKING:
                return QuorumStats.Provider.LOOKING_STATE;
            case LEADING:
                return QuorumStats.Provider.LEADING_STATE;
            case FOLLOWING:
                return QuorumStats.Provider.FOLLOWING_STATE;
            case OBSERVING:
                return QuorumStats.Provider.OBSERVING_STATE;
        }
        return QuorumStats.Provider.UNKNOWN_STATE;
    }





    public long getMyid(){
        return myid;
    }

    public void setMyid(long myid){
        this.myid = myid;
    }

    public int getTickTime(){
        return tickTime;
    }

    public void setTickTime(int tickTime){
        this.tickTime = tickTime;
    }

    public int getMaxClientCnxnPerHost(){
        ServerCnxnFactory fac = getCnxnFactory();
        if(fac == null){
            return -1;
        }
        return fac.getMaxClientCnxnsPerHost();
    }

    public int getMaxSessionTimeout(){
        return minSessionTimeout == -1? tickTime * 2 : minSessionTimeout;
    }

    public void setMaxSessionTimeout(int max){
        this.maxSessionTimeout = max;
    }

    public int getInitLimit(){
        return initLimit;
    }

    public void setInitLimit(int initLimit){
        this.initLimit = initLimit;
    }

    public int getTick(){
        return tick;
    }

    public QuorumVerifier getQuorumVerifier(){
        return quorumConfig;
    }

    public void setQuorumVerifier(QuorumVerifier quorumConfig){
        this.quorumConfig = quorumConfig;
    }

    public Election getElectionAlg(){
        return electionAlg;
    }

    public int getSyncLimit(){
        return syncLimit;
    }

    public void setSyncLimit(int syncLimit){
        this.syncLimit = syncLimit;
    }

    // The syncEnabled can also be set via a system property
    public static final String SYNC_ENABLED = "zookeeper.observer.syncEnabled";

    public boolean getSyncEnabled(){
        if(System.getProperty(SYNC_ENABLED) != null){
            return Boolean.getBoolean(SYNC_ENABLED);
        }else{
            return syncEnabled;
        }
    }

    public void setSyncEnabled(boolean syncEnabled){
        this.syncEnabled = syncEnabled;
    }

    public int getElectionType(){
        return electionType;
    }

    public void setElectionType(int electionType){
        this.electionType = electionType;
    }

    public boolean getQuorumListenOnAllIPs(){
        return quorumListenOnAllIPs;
    }

    public void setQuorumListenOnAllIPs(boolean quorumListenOnAllIPs){
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
    }

    public ServerCnxnFactory getCnxnFactory() { return cnxnFactory; }

    public void setCnxnFactory(ServerCnxnFactory cnxnFactory){
        this.cnxnFactory = cnxnFactory;
    }

    public void setQuorumPeers(Map<Long, QuorumServer> quorumPeers){
        this.quorumPeers = quorumPeers;
    }

    public int getClientPort(){
        return cnxnFactory.getLocalPort();
    }

    public void setClientPortAddress(InetSocketAddress addr){

    }

    public void setTxnFactory(FileTxnSnapLog factory){
        this.logFactory = factory;
    }

    public FileTxnSnapLog getTxnFactory(){
        return this.logFactory;
    }

    // set zk database for this node
    public void setZKDatabase(ZKDatabase database){
        this.zkDb = database;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public boolean isRunning(){
        return running;
    }


    public QuorumCnxManager getQuorumCnxnManager() {
        return qcm;
    }

    public long readLongFromFile(String name) throws Exception{
        File file = new File(logFactory.getSnapDir(), name);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        try{
            line = br.readLine();
            return Long.parseLong(line);
        }catch (Exception e){
            throw new IOException("Found " + line + " in" + file);
        }
        finally {
            br.close();
        }
    }

    public long acceptedEpoch = -1;
    public long currentEpoch = -1;

    public static final String CURRENT_EPOCH_FILENAME = "currentEpoch";
    public static final String ACCEPTED_EPOCH_FILENAME = "acceptedEpoch";
    public static final String UPDATING_EPOCH_FILENAME = "updatingEpoch";

    public void writeLongToFile(String name, long value) throws Exception{
        File file = new File(logFactory.getSnapDir(), name);
        AtomicFileOutputStream out = new AtomicFileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        boolean aborted = false;

        try{
            bw.write(Long.toString(value));
            bw.flush();
            out.flush();
        }catch (IOException e){
            LOG.info("Failed to write new file" + file , e);
            // worst case here the tmp file/resources are not cleaned up
            // and the caller will be notified
            aborted = true;
            out.abort();
            throw e;
        }finally {
            if(!aborted){
                // if the close operation (rename) fail we'll get notified
                // woest case the tmp file may still exist
                out.close();
            }
        }
    }


    public long getCurrentEpoch() throws Exception{
        if(currentEpoch == -1){
            currentEpoch = readLongFromFile(CURRENT_EPOCH_FILENAME);
        }
        return currentEpoch;
    }

    public long getAcceptedEpoch() throws Exception{
        if(acceptedEpoch == -1){
            acceptedEpoch = readLongFromFile(ACCEPTED_EPOCH_FILENAME);
        }
        return acceptedEpoch;
    }

    public void setCurrentEpoch(long e) throws Exception{
        currentEpoch = e;
        writeLongToFile(CURRENT_EPOCH_FILENAME, e);
    }

    public void setAcceptedEpoch(long e) throws Exception{
        acceptedEpoch = e;
        writeLongToFile(ACCEPTED_EPOCH_FILENAME, e);
    }

    /**
     * Updates leader election info to avoid inconsistencies when
     * a new server tries to join the ensemble
     */
    public void updateElectionVote(long newEpoch){
        Vote currentVote = getCurrentVote();
        setBCVote(currentVote);
        if(currentVote != null){
            setCurrentVote(new Vote(currentVote.getId(),
                    currentVote.getZxid(),
                    currentVote.getElectionEpoch(),
                    newEpoch,
                    currentVote.getState()));
        }
    }


}
