package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxnFactory;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumMaj;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

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

    QuorumBean jmxQuorumBean;
    LocalPeerBean jmxLocakPeerbean;
    LeaderElectionBean jmxLeaderElectionBean;
    QuorumCnxManager qcm;

    /**
     * ZKDatabase is a top level member of quorumpeer
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

    Election electionAlg;

    ServerCnxnFactory cnxnFactory;

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

    QuorumStats quorumStats(){
        return quorumStats;
    }

    @Override
    public synchronized void start() {
        loadDataBase();
        cnxnFactory.start;
        startLeaderElection();
        super.start();
    }

    public QuorumPeer(String name) {
        super(name);
    }


    @Override
    public String[] getQuorumPeers() {
        return new String[0];
    }

    @Override
    public String getServerState() {
        return null;
    }


    public void setQuorumListenOnAllIPs(boolean quorumListenOnAllIPs){
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
    }
}
