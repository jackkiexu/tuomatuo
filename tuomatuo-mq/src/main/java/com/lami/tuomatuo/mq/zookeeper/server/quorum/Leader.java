package com.lami.tuomatuo.mq.zookeeper.server.quorum;


import com.lami.tuomatuo.mq.zookeeper.server.FinalRequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumVerifier;
import com.lami.tuomatuo.mq.zookeeper.server.util.ZxidUtils;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class has the control logic for the leader
 * Created by xujiankang on 2017/3/19.
 */
public class Leader {

    private static final Logger LOG = LoggerFactory.getLogger(Leader.class);

    static final private boolean nodelay = System.getProperty("leader.nodelay", "true").equals("true");

    static {
        LOG.info("TCP NoDelay set to : " + nodelay);
    }

    static public class Proposal{
        public QuorumPacket packet;

        public HashSet<Long> ackSet = new HashSet<>();

        public Request request;

        @Override
        public String toString() {
            return "Proposal{" +
                    "packet=" + packet +
                    ", ackSet=" + ackSet +
                    ", request=" + request +
                    '}';
        }
    }

    public LeaderZooKeeperServer zk;

    public QuorumPeer self;

    private boolean quorumFormed = false;

    public LearnerCnxAcceptor cnxnAcceptor;
    // list of all the followers
    private final HashSet<LearnerHandler> learners = new HashSet<>();


    public List<LearnerHandler> getLearners(){
        synchronized (learners){
            return new ArrayList<LearnerHandler>(learners);
        }
    }

    // list of followers are already to follow (i.e synced with the leader)
    private final HashSet<LearnerHandler> forwardingFollowers = new HashSet<>();

    public List<LearnerHandler> getForwardingFolllowers(){
        synchronized (forwardingFollowers){
            return new ArrayList<LearnerHandler>(forwardingFollowers);
        }
    }

    private void addForwardingFollower(LearnerHandler lh){
        synchronized (forwardingFollowers){
            forwardingFollowers.add(lh);
        }
    }

    private final HashSet<LearnerHandler> observingLearners = new HashSet<>();

    public List<LearnerHandler> getObservingLearners(){
        synchronized (observingLearners){
            return new ArrayList<LearnerHandler>(observingLearners);
        }
    }

    public void addObserverLearnerHandler(LearnerHandler lh){
        synchronized (observingLearners){
            observingLearners.add(lh);
        }
    }

    // Pending sync requests. Must access under this lock
    public final HashMap<Long, List<LearnerSyncRequest>> pendingSyncs = new HashMap<>();

    synchronized public int getNumPendingSyncs(){
        return pendingSyncs.size();
    }

    synchronized public int getNumPendingSync(){
        return pendingSyncs.size();
    }

    // Follower counter
    final AtomicLong followerCounter = new AtomicLong(-1);


    void addLearnerHandler(LearnerHandler learnerHandler){
        synchronized (learners){
            learners.add(learnerHandler);
        }
    }

    /**
     * Remove the learner from the learner list
     */
    void removeLearnerHandler(LearnerHandler peer){
       synchronized (forwardingFollowers){
           forwardingFollowers.remove(peer);
       }
        synchronized (learners){
            learners.remove(peer);
        }

        synchronized (observingLearners){
            observingLearners.remove(peer);
        }
    }

    boolean isLearnerSynced(LearnerHandler peer){
        synchronized (forwardingFollowers){
            return forwardingFollowers.contains(peer);
        }
    }

    ServerSocket ss;


    Leader(QuorumPeer self, LeaderZooKeeperServer zk) throws IOException{
        this.self = self;
        try{
            if(self.getQuorumListenOnAllIPs()){
                ss = new ServerSocket(self.getQuorumAddress().getPort());
            }else{
                ss = new ServerSocket();
            }
            ss.setReuseAddress(true);
            if(!self.getQuorumListenOnAllIPs()){
                ss.bind(self.getQuorumAddress());
            }
        }catch (Exception e){
            if(self.getQuorumListenOnAllIPs()){
                LOG.info("Couldn't bind to port " + self.getQuorumAddress().getPort(), e);
            }else{
                LOG.info("Couldn't bind to " + self.getQuorumAddress(), e);
            }
            throw e;
        }
        this.zk = zk;
    }

    // This message is for follower to expect diff
    public final static int DIFF = 13;

    // This is for follower to truncate its logs
    public final static int TRUNC = 14;

    // This is for follower to download the snapshots
    public final static int SNAP = 15;

    // This tells the leader that the connecting peer is actually an observer
    public final static int OBSERVERINFO = 16;

    // This message type is sent by the leader to indicate it's zxid and if
    // needed, its database
    public final static int NEWLEADER = 10;

    // This message type is sent by the leader to indicate it's zxid and if
    // needed, its database
    public final static int FOLLOWERINFO = 11;

    /**
     * This message type is sent by the leader to indicate that the follower is
     * now uptodate and can start responding to client
     */
    public final static int UPTODATE = 12;

    /**
     * This message is the first that a follower receives from the leader
     * It has the protocol version and the epoch of the leader
     */
    public final static int LEADERINFO = 17;

    // This message is used by the follow to ack a proposed epoch
    public final static int ACKEPOCH = 18;

    /**
     * This message type is sent to a leader to request and mutation operation
     * The payload will consist of a request header followed by a request
     */
    public final static int REQUEST = 1;

    /**
     * this message type is sent by a leader to propose a mutation
     */
    public final static int PROPOSAL = 2;

    /**
     * This message type is sent by a follower after it has synced a proposal
     */
    final static int ACK = 3;

    /**
     * This message type is sent by a leader to commit a proposal and cause
     * followers to start serving the corresponding data
     */
    final static int COMMIT = 4;

    /**
     * This message type is enchanged bwtween follower and leader (initiated by
     * follower) to determine liveliness
     */
    final static int PING = 5;

    /**
     * This message type is to validate a session that should be active
     */
    final static int REVALIDATE = 6;

    /**
     * This message is a reply to a synchronize command flushing the pipe
     * between the leader and the follower
     */
    final static int SYNC = 7;

    /**
     * This message type informs observers of a committed proposal
     */
    final static int INFORM = 8;

    ConcurrentMap<Long, Proposal> outstandingProposals = new ConcurrentHashMap<>();

    ConcurrentLinkedQueue<Proposal> toBeApplied = new ConcurrentLinkedQueue<>();

    Proposal newLeaderProposal = new Proposal();



    class LearnerCnxAcceptor extends Thread{

        private volatile boolean stop = false;

        @Override
        public void run() {
            try{
                Socket s = ss.accept();
                // start with the initLimit, once the ack is processed
                // in LearnerHandler switch to the syncLimit
                s.setSoTimeout(self.tickTime * self.initLimit);
                s.setTcpNoDelay(nodelay);
                LearnerHandler fh = new LearnerHandler(s, Leader.this);
                fh.start();
            }catch (Exception e){
                if(stop){
                    LOG.info("exception while shutting down acceptor:" + e);
                    // When Leader.shutdown() calls ss.close()
                    // the call to accept throwns an exception
                    // we catch and set stop to true
                    stop = true;
                }
            }
        }

        public void halt(){
            stop = true;
        }
    }

    public StateSummary leaderStateSummary;

    public long epoch = -1;
    public boolean waitingForNewEpoch = true;
    public volatile boolean readyToStart = false;

    /**
     * This method is main function that is called to lead
     * @throws Exception
     */
    void lead() throws Exception{
        self.end_fle = System.currentTimeMillis();
        LOG.info("LEADING - LEADER ELECTION TOOK - " + (self.end_fle - self.start_fle));

        self.start_fle = 0;
        self.end_fle = 0;

        zk.registerJMX(new LeaderBean(this, zk), self.jmxLocakPeerbean);

        try{
            self.tick = 0;
            zk.loadData();

            leaderStateSummary = new StateSummary(self.getCurrentEpoch(), zk.getLastProcessedZxid());

            // Start thread that waits for connection request request from new followers
            cnxnAcceptor = new LearnerCnxAcceptor();
            cnxnAcceptor.start();

            readyToStart = true;
            long epoch = getEpochToPropose(self.getId(), self.getAcceptedEpoch());

            zk.setZxid(ZxidUtils.makeZxid(epoch, 0));

            synchronized (this){
                lastProposed = zk.getZxid();
            }

            newLeaderProposal.packet = new QuorumPacket(NEWLEADER, zk.getZxid(), null, null);
            if ((newLeaderProposal.packet.getZxid() & 0xffffffffL) != 0) {
                LOG.info("NEWLEADER proposal has Zxid of "
                        + Long.toHexString(newLeaderProposal.packet.getZxid()));
            }

            waitForEpochAck(self.getId(), leaderStateSummary);
            self.setCurrentEpoch(epoch);


            /**
             * We have to get at least a majority of servers in sync with
             * us. We do this by waiting for the NEWLEADER packet to get
             * acknowledged
             */
            try{
                waitForNewLeaderAck(self.getId(), zk.getZxid(), QuorumPeer.LearnerType.PARRTICIPANT);
            }catch (Exception e){

                shutdown("Waiting for a quorum of followers, only synced with sids:["
                        + getSidSetString(newLeaderProposal.ackSet) + " ]");
                HashSet<Long> followerSet = new HashSet<>();
                for(LearnerHandler f : learners){
                    followerSet.add(f.getSid());
                }

                Thread.sleep(self.tickTime);
                self.tick++;
                return;
            }

            startZkServer();

            String initialZxid = System.getProperty("zookeeper.testingonly.initialZxid");
            if(initialZxid != null){
                long zxid = Long.parseLong(initialZxid);
                zk.setZxid((zk.getZxid() & 0xffffffff00000000L) | zxid);
            }

            if(!System.getProperty("zookeeper.leaderServes", "yes").equals("no")){
                self.cnxnFactory.setZooKeeperServer(zk);
            }

            boolean tickSkip = true;

            while(true){
                Thread.sleep(self.tickTime / 2);
                if(!tickSkip){
                    self.tick++;
                }

                HashSet<Long> syncedSet = new HashSet<>();

                // lock on the followers when we use it

                syncedSet.add(self.getId());

                for(LearnerHandler f : getLearners()){
                    if(f.synced() && f.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
                        syncedSet.add(f.getSid());
                    }
                    f.ping();
                }

                if(!tickSkip && !self.getQuorumVerifier().containsQuorum(syncedSet)){
                    shutdown("Not sufficient followers synced, only synced with sids: [ "
                            + getSidSetString(syncedSet) + " ]");
                    // make sure the order is the same!
                    // the leader goes to looking
                    return;
                }

                tickSkip = !tickSkip;
            }


        }finally {
            zk.unregisterJMX(this);
        }
    }

    // Close down all the LearnerHandlers
    void shutdown(String reason){
        LOG.info("Shtting down");
        if(isShutdown){
            return;
        }

        LOG.info("Shutdown called", new Exception("shutdown Leader!reason:" + reason));
        if(cnxnAcceptor != null){
            cnxnAcceptor.halt();
        }


        // NIO should not accept connections
        self.cnxnFactory.setZooKeeperServer(null);

        try{
            ss.close();
        }catch (Exception e){
            LOG.warn("Ignoring unexpected exception during close");
        }

        // clear all the connections
        self.cnxnFactory.closeAll();

        // shutdown the previous zk
        if(zk != null){
            zk.shutdown();
        }

        synchronized (learners){
            for(Iterator<LearnerHandler>it = learners.iterator(); it.hasNext();){
                LearnerHandler f = it.next();
                it.remove();
                f.shutdown();
            }
        }

        isShutdown = true;
    }

    synchronized public void processAck(long sid, long zxid, SocketAddress followerAddr){
        if ((zxid & 0xffffffffL) == 0) {
            /**
             * We no longer process NEWLEADER ack by this method. However,
             * the learner sends ack back to the leader after it gets UPTODATE
             * se we just ignore the message
             */
            return;
        }

        if(outstandingProposals.size() == 0){
            return;
        }

        if(lastCommitted >= zxid){
            // The proposal has already been committed
            return;
        }

        Proposal p = outstandingProposals.get(zxid);
        if(p == null){
            LOG.warn("Trying to commit future proposal: zxid 0x{} from {}",
                    Long.toHexString(zxid), followerAddr);
            return;
        }

        p.ackSet.add(sid);

        if(self.getQuorumVerifier().containsQuorum(p.ackSet)){
            if(zxid != lastCommitted + 1){
                LOG.warn("Commiting zxid 0x{} from {} not first!",
                        Long.toHexString(zxid), followerAddr);
                LOG.warn("First is 0x{}", Long.toHexString(lastCommitted + 1));
            }

            outstandingProposals.remove(zxid);
            if(p.request != null){
                toBeApplied.add(p);
            }

            if(p.request == null){
                LOG.warn("Going to commit null request for proposal: {}", p);
            }

            commit(zxid);
            inform(p);
            zk.commitProcessor.commit(p.request);
            if(pendingSyncs.containsKey(zxid)){
                for(LearnerSyncRequest r : pendingSyncs.remove(zxid)){
                    sendSync(r);
                }
            }
        }
    }


    static class ToBeAppliedRequestProcessor implements RequestProcessor{

        private RequestProcessor next;

        private ConcurrentLinkedQueue<Proposal> toBeApplied;

        /**
         * This request processor simply maintains the tobeApplied list. For
         * this to work next must be a FinalRequestProcessor and
         * FinalRequestProcessor.processRequest MUST process the request
         * synchronously
         */
        public ToBeAppliedRequestProcessor(RequestProcessor next, ConcurrentLinkedQueue<Proposal> toBeApplied) {
            if(!(next instanceof FinalRequestProcessor)){
                throw new RuntimeException(ToBeAppliedRequestProcessor.class.getName()
                        + " must be connected to "
                        + FinalRequestProcessor.class.getName()
                        + " not "
                        + next.getClass().getName()
                );
            }

            this.next = next;
            this.toBeApplied = toBeApplied;
        }

        @Override
        public void processRequest(Request request) throws RequestProcessorException {
            next.processRequest(request);
            Proposal p = toBeApplied.peek();
            if(p != null && p.request != null
                    && p.request.zxid == request.zxid){
                toBeApplied.remove();
            }
        }

        @Override
        public void shutdown() {
            LOG.info("Shutting down");
            next.shutdown();
        }
    }


    /**
     * send a packet to all the follower ready to follow
     * @param qp
     */
    void sendPacket(QuorumPacket qp){
        synchronized (forwardingFollowers){
            for(LearnerHandler f : forwardingFollowers){
                f.queuePacket(qp);
            }
        }
    }


    void sendObserverpacket(QuorumPacket qp){
        for(LearnerHandler f : getObservingLearners()){
            f.queuePacket(qp);
        }
    }
    public long lastCommitted = -1;

    public void commit(long zxid){
        synchronized (this){
            lastCommitted = zxid;
        }

        QuorumPacket qp = new QuorumPacket(Leader.COMMIT, zxid, null, null);
        sendPacket(qp);
    }

    /**
     * Create an inform packet and send it ti all observers
     * @param proposal
     */
    public void inform(Proposal proposal){
        QuorumPacket qp = new QuorumPacket(Leader.INFORM, proposal.request.zxid,
                proposal.packet.getData(), null);
        sendObserverpacket(qp);
    }

    public boolean isShutdown;
    public long lastProposed;


    public long getEpoch(){
        return ZxidUtils.getEpochFromZxid(lastProposed);
    }

    public static class XidRolloverException extends Exception{
        public XidRolloverException(String message) {
            super(message);
        }
    }

    /**
     * create a proposal and send it out to all the members
     */
    public Proposal propose(Request request) throws Exception{
        /**
         * Address the rollover. All lower 32bits set indicate a new leader
         * election
         */
        if ((request.zxid & 0xffffffffL) == 0xffffffffL) {
            String msg = "zxid lower 32 bits have rolled over, forcing re-election, and therefore new epoch start";
            shutdown(msg);
            throw new XidRolloverException(msg);
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        try{
            request.hdr.serialize(boa, "hdr");
            if(request.txn != null){
                request.txn.serialize(boa, "txn");
            }
            baos.close();
        }catch (Exception e){
            LOG.warn("This really should be impossible", e);
        }

        QuorumPacket pp = new QuorumPacket(Leader.PROPOSAL, request.zxid,
                baos.toByteArray(), null);

        Proposal p = new Proposal();
        p.packet = pp;
        p.request = request;
        synchronized (this){
            LOG.info("Proposing:" + request);
            lastProposed = p.packet.getZxid();
            outstandingProposals.put(lastProposed, p);
            sendPacket(pp);
        }
        return p;
    }
    
    // Process sync requests
    synchronized public void processSync(LearnerSyncRequest r){
        if(outstandingProposals.isEmpty()){
            sendSync(r);
        }
        else{
            List<LearnerSyncRequest> l = pendingSyncs.get(lastProposed);
            if(l == null){
                l = new ArrayList<LearnerSyncRequest>();
            }
            l.add(r);
            pendingSyncs.put(lastProposed, l);
        }
    }


    // send a sync message to the appropriate server
    public void sendSync(LearnerSyncRequest r){
        QuorumPacket qp = new QuorumPacket(Leader.SYNC, 0, null, null);
        r.fh.queuePacket(qp);
    }

    /**
     * Lets the leader know that a follower is capable of following and is done
     * syncing
     */
    synchronized public long startForwarding(LearnerHandler handler, long lastSeenZxid){
        // Queue up any outstanding requests enabling the receipt of
        // new requests
        if(lastProposed > lastSeenZxid){
            for(Proposal p : toBeApplied){
               if(p.packet.getZxid() <= lastSeenZxid){
                   continue;
               }
               handler.queuePacket(p.packet);
                // Since the proposal has been committed we need to send the
                // commit message also
                QuorumPacket qp = new QuorumPacket(Leader.COMMIT, p.packet.getZxid(),
                         null, null);
                handler.queuePacket(qp);
            }

            // only participant need to get outstanding proposals
            if(handler.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
                List<Long> zxids = new ArrayList<>(outstandingProposals.keySet());
                Collections.sort(zxids);
                for(Long zxid : zxids){
                    if(zxid <= lastSeenZxid){
                        continue;
                    }
                    handler.queuePacket(outstandingProposals.get(zxid).packet);
                }
            }
        }

        if(handler.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
            addForwardingFollower(handler);

        }else{
            addObserverLearnerHandler(handler);
        }
        return lastProposed;
    }

    private HashSet<Long> connectingFollowers = new HashSet<>();
    public long getEpochToPropose(long sid, long lastAcceptedEpoch) throws Exception{
        synchronized (connectingFollowers){
            if(!waitingForNewEpoch){
                return epoch;
            }

            if(lastAcceptedEpoch >= epoch){
                epoch = lastAcceptedEpoch + 1;
            }

            connectingFollowers.add(sid);
            QuorumVerifier verifier = self.getQuorumVerifier();
            if(connectingFollowers.contains(self.getId()) &&
            verifier.containsQuorum(connectingFollowers)){
                waitingForNewEpoch = false;
                self.setAcceptedEpoch(epoch);
                connectingFollowers.notifyAll();
            }
            else{
                long start = System.currentTimeMillis();
                long cur = start;
                long end = start + self.getInitLimit() * self.getTickTime();
                while(waitingForNewEpoch && cur < end){
                    connectingFollowers.wait(end - cur);
                    cur = System.currentTimeMillis();
                }
                if(waitingForNewEpoch){
                    throw new Exception("Timeout while waiting for epoch from quorum");
                }
            }

            return epoch;
        }
    }


    private HashSet<Long> electionFollowers = new HashSet<>();
    private boolean electionFinished = false;
    public void waitForEpochAck(long id, StateSummary ss) throws Exception{
        synchronized (electionFollowers){
            if(electionFinished){
                return;
            }

            if(ss.getCurrentEpoch() != -1){
                if(ss.isMoreRecentThan(leaderStateSummary)){
                    throw new IOException("Follower is ahead of the leader, leader summary:"
                            + leaderStateSummary.getCurrentEpoch()
                            + " (current epoch) "
                            + leaderStateSummary.getLastZxid()
                            + " (last zxid) "
                    );
                }

                electionFollowers.add(id);
            }

            QuorumVerifier verifier = self.getQuorumVerifier();
            if(electionFollowers.contains(self.getId()) && verifier.containsQuorum(electionFollowers)){
                electionFinished = true;
                electionFollowers.notifyAll();
            }
            else{
                long start = System.currentTimeMillis();
                long cur = start;
                long end = start + self.getInitLimit() * self.getTickTime();
                while(!electionFinished && cur < end){
                    electionFollowers.wait(end - cur);
                    cur = System.currentTimeMillis();
                }

                if(!electionFinished){
                    throw new Exception("Timeout while waiting for epoch to be acked by quorum");
                }
            }
        }
    }


    public String getSidSetString(Set<Long> sidSet){
        StringBuilder sids = new StringBuilder();
        Iterator<Long> iter = sidSet.iterator();
        while(iter.hasNext()){
            sids.append(iter.next());
            if(!iter.hasNext()){
                break;
            }
            sids.append(",");
        }
        return sids.toString();
    }

    // Start up leader ZooKeeper server and initialize zxid to the new epoch
    private synchronized void startZkServer(){
        // Update lastCommitted and Db's zxid to a value representing the new epoch
        lastCommitted = zk.getZxid();

        LOG.info("Have quorum of supporters, sids: [ "
                        + getSidSetString(newLeaderProposal.ackSet)
                        + " ]; starting up and setting last processed zxid: 0x{}",
                Long.toHexString(zk.getZxid()));
        zk.startup();

        self.updateElectionVote(getEpoch());
        zk.getZKDatabase().setlastProcessedZxid(zk.getZxid());
    }


    /**
     * Process NEWLEADER ack of a given sid and wait until the leader receives
     */
    public void waitForNewLeaderAck(long sid, long zxid, QuorumPeer.LearnerType learnerType)throws Exception{
        synchronized (newLeaderProposal.ackSet){
            if(quorumFormed){
                return;
            }

            long currentZxid = newLeaderProposal.packet.getZxid();
            if(zxid != currentZxid){
                LOG.info("NEWLEADER ACK from sid:" + sid
                    + " is from a different epoch - current 0x"
                        + Long.toHexString(currentZxid) + " received 0x"
                        + Long.toHexString(zxid)
                );
                return;
            }

            if(learnerType == QuorumPeer.LearnerType.PARRTICIPANT){
                newLeaderProposal.ackSet.add(sid);
            }

            if(self.getQuorumVerifier().containsQuorum(newLeaderProposal.ackSet)){
                quorumFormed = true;
                newLeaderProposal.ackSet.notifyAll();
            }
            else{
                long start = System.currentTimeMillis();
                long cur = start;
                long end = start + self.getInitLimit() * self.getTickTime();
                while(!quorumFormed && cur < end){
                    newLeaderProposal.ackSet.wait(end - cur);
                    cur = System.currentTimeMillis();
                }
                if(!quorumFormed){
                    throw new InterruptedException(
                            "Timeout while waiting for NEWLEADER to be acked by quorum"
                    );
                }
            }
        }
    }

    /**
     * Get string representation of a given packet type
     * @param packetType
     * @return
     */
    public static String getPacketType(int packetType){
        switch (packetType){
            case DIFF:
                return "DIFF";
            case TRUNC:
                return "TRUNC";
            case SNAP:
                return "SNAP";
            case OBSERVERINFO:
                return "OBSERVERINFO";
            case NEWLEADER:
                return "NEWLEADER";
            case FOLLOWERINFO:
                return "FOLLOWERINFO";
            case UPTODATE:
                return "UPTODATE";
            case LEADERINFO:
                return "LEADERINFO";
            case ACKEPOCH:
                return "ACKEPOCH";
            case REQUEST:
                return "REQUEST";
            case PROPOSAL:
                return "PROPOSAL";
            case ACK:
                return "ACK";
            case COMMIT:
                return "COMMIT";
            case PING:
                return "PING";
            case REVALIDATE:
                return "REVALIDATE";
            case SYNC:
                return "SYNC";
            case INFORM:
                return "INFORM";
            default:
                return "UNKNOW";
        }
    }

}
