package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.jmx.MBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of leader election using TCP. It uses an object of the class
 * QuorumCnxmanager to manage connections. Otherwise, the algorithm is push-based
 * as with the other UDP implementations
 *
 * There are a few parameters that can be tuned to change its behavior. First
 * finalizeWait determines the amount of time to wait until deciding upon a leader
 * This is part of the leader election algorithm
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FastleaderElection implements Election {

    private static final Logger LOG = LoggerFactory.getLogger(FastleaderElection.class);

    /**
     * Determine how much time a process has to wait
     * once it believes that it has reached the end of
     * leader election
     */
    public final static int finalizeWait = 200;

    /**
     * Upper bound on the amount of time between two consecutive(连续)
     * notification checks. This impacts the amount of time to get
     * the system up again after long partitions Currently 60 seconds
     */
    public final static int maxNotificationInterval = 60000;

    /**
     * Connection manager. Fast leader election uses TCP for
     * communication between peers. and QuorumCnxManager manages
     * such connections
     */
    public QuorumCnxManager manager;

    /**
     * Notifications are messages that let other peers know that
     * a given peer has changed its vote, either because it has
     * joined leader election or because it learned of another
     * peer with higher zxid or same zxid and higher server id
     */
    static public class Notification{
        // version 3.4.6
        public final static int CURRENTVERSION = 0x1;

        public int version;

        // Proposed leader
        public long leader;

        // zxid of the proposed leader
        public long zxid;

        // Epoch
        public long electionEpoch;

        /// current state of sender
        public QuorumPeer.ServerState state;

        // Address of sender
        public long sid;

        // epoch of the proposed leader
        public long peerEpoch;

        @Override
        public String toString() {
            return "Notification{" +
                    "version=" + version +
                    ", leader=" + leader +
                    ", zxid=" + zxid +
                    ", electionEpoch=" + electionEpoch +
                    ", state=" + state +
                    ", sid=" + sid +
                    ", peerEpoch=" + peerEpoch +
                    '}';
        }
    }

    static ByteBuffer buildMsg(int state,
                               long leader,
                               long zxid,
                               long electionEpoch,
                               long epoch){
        byte requestBytes[] = new byte[40];
        ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);

        // Building notification packet to send
        requestBuffer.clear();
        requestBuffer.putInt(state);
        requestBuffer.putLong(leader);
        requestBuffer.putLong(zxid);
        requestBuffer.putLong(electionEpoch);
        requestBuffer.putLong(epoch);
        requestBuffer.putInt(Notification.CURRENTVERSION);

        return requestBuffer;
    }

    /**
     * Messages that a peer wants to send to other peers
     * These messages can be both Notifications and Acks
     * if reception of notification
     */
    static public class ToSend{
        static enum mType{ crequest, challenge, notification, ack }

        ToSend(mType type,
               long leader,
               long zxid,
               long electionEpoch,
               QuorumPeer.ServerState state,
               long sid,
               long peerEpoch) {

            this.leader = leader;
            this.zxid = zxid;
            this.electionEpoch = electionEpoch;
            this.state = state;
            this.sid = sid;
            this.peerEpoch = peerEpoch;
        }

        // Proposed leader in the case of notification
        public long leader;

        // id contains the tag for acks, and zxid for notifications
        public long zxid;

        // Epoch
        public long electionEpoch;

        // Current state
        QuorumPeer.ServerState state;

        // Address of recipient
        public long sid;

        // Leader epoch
        public long peerEpoch;
    }


    public LinkedBlockingQueue<ToSend> sendqueue;
    public LinkedBlockingQueue<Notification> recvqueue;

    /**
     * Multi-threaded implementation of message handler. Messager
     * implements two sub-classes. WorkReceiver and WorkSender. The
     * functionality of each is obious from the name. Each of these
     * spawns a new thread
     */
    protected class Messenger {

        /**
         * Receive message from instance of QuorumCnxmanager on
         * method run(), and processes such messages
         */
        class WorkerReceiver implements Runnable{

            public boolean stop;

            QuorumCnxManager manager;

            public WorkerReceiver(QuorumCnxManager manager) {
                this.stop = false;
                this.manager = manager;
            }

            public void run() {

            }
        }


        /**
         * This worker simply dequeues a message to send and
         * queues it on the manager's queue
         */

        class WorkerSender implements Runnable{

            public volatile boolean stop;
            public QuorumCnxManager manager;

            public WorkerSender(QuorumCnxManager manager) {
                this.stop = false;
                this.manager = manager;
            }

            @Override
            public void run() {
                while(!stop){
                    try{
                        ToSend m = sendqueue.poll(3000, TimeUnit.MILLISECONDS);
                        if(m == null){
                            continue;
                        }
                        process(m);
                    }catch (Exception e){
                        break;
                    }
                }
            }

            // Called by run() once there is a new message to send
            void process(ToSend m){
                ByteBuffer requestBuffer = buildMsg(m.state.ordinal(),
                        m.leader,
                        m.zxid,
                        m.electionEpoch,
                        m.peerEpoch);
                manager.toSend(m.sid, requestBuffer);
            }
        }


        // Test if both send and receive queues are empty
        public boolean queueEmpty(){
            return (sendqueue.isEmpty() || recvqueue.isEmpty());
        }

        public WorkerSender ws;
        public WorkerReceiver wr;


        // stops instances of WorkSender and Worker receiver
        public void halt(){
            this.ws.stop = true;
            this.wr.stop = true;
        }
    }


    public QuorumPeer self;

    public Messenger messenger;
    public volatile long logicalclock; // Election instance
    public long proposedLeader;
    public long proposedZxid;
    public long proposedEpoch;

    // Returns the current valve of the logical clock counter
    public long getLogicalclock(){
        return logicalclock;
    }

    public FastleaderElection(QuorumPeer self, QuorumCnxManager manager) {
        this.stop = false;
        this.manager = manager;
        starter(self, manager);
    }

    /**
     * This method is invoked by the constructor. Because it is a
     * part of the starting procedure of this object that must be on
     * any constructor of this class. it is probably best to keep as
     * a separate method, as we have a single constructor currently
     * it is not strictly necessary to have it separate
     */
    private void starter(QuorumPeer self, QuorumCnxManager manager){
        this.self = self;
        proposedLeader = -1;
        proposedZxid = -1;

        sendqueue = new LinkedBlockingQueue<ToSend>();
        recvqueue = new LinkedBlockingQueue<Notification>();
        this.messenger = new Messenger(manager);
    }

    private void leaveInstance(Vote v){
        LOG.info("About to leave FLE instance : leader ="
                + v.getId() + ", zxid = 0x " +
        Long.toHexString(v.getZxid()) + ", my id = " + self.getId()
        + ", my state = " + self.getPeerState());

        recvqueue.clear();
    }

    public QuorumCnxManager getCnxManager() { return manager; }

    public volatile  boolean stop;

    public void shutdown(){
        stop = true;
        LOG.info("Shutting down connection manager");
        manager.halt();
        LOG.info("Shutting down messenger");
        messenger.halt();
        LOG.info("FILE is down");
    }


    public void sendNotifications(){
        for(QuorumPeer.QuorumServer server : self.getVotingView().values()){
            long sid = server.id;
            ToSend notmsg = new ToSend(ToSend.mType.notification,
                    proposedLeader,
                    proposedZxid,
                    logicalclock,
                    QuorumPeer.ServerState.LOOKING,
                    sid,
                    proposedEpoch);
            if(LOG.isDebugEnabled()){
                LOG.debug("Sending Notification: " + proposedLeader + " (n.leader), 0x"  +
                        Long.toHexString(proposedZxid) + " (n.zxid), 0x" + Long.toHexString(logicalclock)  +
                        " (n.round), " + sid + " (recipient), " + self.getId() +
                        " (myid), 0x" + Long.toHexString(proposedEpoch) + " (n.peerEpoch)");
            }

            sendqueue.offer(notmsg);
        }
    }

    public void printNotification(Notification n){
        LOG.info("Notification :" + n.toString()
        + self.getPeerState() + " (my state)");
    }

    public boolean totalOrderPredicate(long newId, long newZxid, long epoch, long curid, long curZxid, long curEpoch){
        if(self.getQuorumVerifier().getWeight(newId) == 0){
            return false;
        }

        /**
         * We return if one of the following three cases hold :
         * 1- NEW epoch is higher
         * 2- New epoch is the same as current epcoh. but new zxid is higher
         * 3- NEW epoch is the same as current epoch, new zxid is the same
         * as current zxid, but server id is higher
         */
        return ((newEpoch > curEpoch) ||
                ((newEpoch == curEpoch) &&
                        ((newZxid > curZxid) || ((newZxid == curZxid) && (newId > curid)))));
    }

    /**
     * Termination predicate Given a set of votes, determines if
     * have sufficient to declare the end of the election round
     * @param votes
     * @param vote
     * @return
     */
    public boolean termPredicate(HashMap<Long, Vote> votes, Vote vote){
        HashSet<Long> set = new HashSet<>();

        /**
         * First make the views consistent. Sometimes peers will have
         * different zxids for a server depending on timing
         */
        for(Map.Entry<Long, Vote> entry : votes.entrySet()){
            if(vote.equals(entry.getValue())){
                set.add(entry.getKey());
            }
        }

        return self.getQuorumVerifier().containsQuorum(set);
    }

    /**
     * In the case there is a leader elected, and a quorum supporting
     * this leader, we have to check if the leader has voted and acked
     * that it is leading. We need this check to avoid that peers keep
     * electing over and over a peer that has crashed and it is no
     * longer leading
     * @param votes
     * @param leader
     * @param electionEpoch
     * @return
     */
    public boolean checkLeader(HashMap<Long, Vote> votes,
                               long leader,
                               long electionEpoch){
        /**
         * If everyone lese thinks I'm the leader. I must be the leader.
         * The other two checks are just for the case in which I'm not the
         * leader. If i'm not the leader and I haven't received a message
         * from leader standing that it is leading then predicate is false
         */

        boolean predicate = true;

        if(leader != self.getId()){
            if(votes.get(leader) == null){
                predicate = false;
            }
            else if(votes.get(leader).getState() != QuorumPeer.ServerState.LEADING){
                predicate = false;
            }
        }
        else if(logicalclock != electionEpoch){
            predicate = false;
        }

        return predicate;
    }

    /**
     * This predicate checks that a leader has been elected. It doesn't
     * make a lot of sence without context (check lookForLeader) and it
     * has been separated for testing purpose
     */
    protected boolean ooePredicate(HashMap<Long, Vote> recv,
                                   HashMap<Long, Vote> ooe,
                                   Notification n){
        return (termPredicate(recv, new Vote(n.version,
                                                n.leader,
                                                n.zxid,
                                                n.electionEpoch,
                                                n.peerEpoch,
                                                n.state
                ))
        && checkLeader(ooe, n.leader, n.electionEpoch));
    }

    synchronized void updateProposal(long leader, long zxid, long epoch){
        proposedLeader = leader;
        proposedZxid = zxid;
        proposedEpoch = epoch;
    }

    synchronized Vote getVote(){
        return new Vote(proposedLeader, proposedZxid, proposedEpoch);
    }

    /**
     * A learning state can be either FOLLOWER or OBSERVER
     * This method simply decides which one depending on the
     * role of the server
     */
    public QuorumPeer.ServerState learningState(){
        if(self.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
            LOG.info("I'm a participant: " + self.getId());
            return QuorumPeer.ServerState.FOLLOWING;
        }
        else{
            LOG.info("I'm an observer: " + self.getId());
            return QuorumPeer.ServerState.OBSERVING;
        }
    }

    // return the initial vote value of server identifier
    public long getInitId(){
        if(self.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
            return self.getId();
        }
        else{
            return Long.MIN_VALUE;
        }
    }

    /**
     * Returns initial last logged zxid
     */
    public long getInitLastLoggedZxid(){
        if(self.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
            return self.getLastLoggedZxid();
        }
        else{
            return Long.MIN_VALUE;
        }
    }

    // Return the initial vote of the peer epoch
    public long getPeerEpoch(){
        if(self.getLearnerType() == QuorumPeer.LearnerType.PARRTICIPANT){
            try{
                return self.getCurrentEpoch();
            }catch (Exception e){
                throw e;
            }
        }else{
            return Long.MIN_VALUE;
        }
    }

    /**
     * Starts a new round of leader election. Whenever our QuorumPeer
     * changes its state to LOOKING, this method is invoked, and it
     * sends notification to all other peers
     */
    @Override
    public Vote lookForleader() throws InterruptedException {
        try{

        }catch (Exception e){

        }


        try{
            HashMap<Long, Vote> recvset = new HashMap<>();
            HashMap<Long, Vote> outofelection = new HashMap<>();
            int notTimeout = finalizeWait;

            synchronized (this){
                logicalclock++;
                updateProposal(getInitId(), getInitLastLoggedZxid(), getPeerEpoch());
            }

            LOG.info("New election My id=" + self.getId()
            + ", proposed zxid=0x" + Long.toHexString(proposedZxid));

            sendNotifications();
            // Loop in which we exchange notifications until we find a leader

            while((self.getPeerState() == QuorumPeer.ServerState.LOOKING) &&
                    (!stop)){
                // remove next notification from queue, times out after 2 time
                // the termination time
                Notification n = recvqueue.poll(notTimeout, TimeUnit.MILLISECONDS);

                // Sends more notifivation if haven't received enough
                // other wise process new notification

                if(n == null){
                    if(manager.haveDelivered()){
                        sendNotifications();
                    }
                    else{
                        manager.connectAll();
                    }

                    // Exponential backoff
                    int tmpTimeOut = notTimeout * 2;
                    notTimeout = (tmpTimeOut < maxNotificationInterval?
                    tmpTimeOut : maxNotificationInterval);
                    LOG.info("Notification timeout:" + notTimeout);
                }
                else if(self.getVotingView().containsKey(n.sid)){
                    // Only proceed if the vote comes from a replica in the
                    // voting view
                    switch (n.state){
                        case LOOKING:{
                            break;
                        }
                        case OBSERVING:{
                            break;
                        }
                        case FOLLOWING:
                        case LEADING:{

                        }
                        default:{
                            LOG.warn("Notification state unrecognized:" + n.state+ ", " + n.sid);
                            break;
                        }

                    }
                }
                else{
                    LOG.warn("Ignoring notification from non-cluster member " + n.sid);
                }
            }

            return null;
        }finally {
            try{
                if(self.jmxLeaderElectionBean != null){
                    MBeanRegistry.getInstance().unregister(self.jmxLeaderElectionBean);
                }
            }catch (Exception e){
                LOG.info("Failed to unregister with JMX", e);
            }
            self.jmxLeaderElectionBean = null;
        }
        return null;
    }

    @Override
    public void shutdown() {

    }
}
