package com.lami.tuomatuo.mq.zookeeper.server.quorum;


import com.lami.tuomatuo.mq.zookeeper.server.Request;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    LeaderZooKeeperServer zk;

    QuorumPeer self;

    private boolean quorumFormed = false;

    public LearnerCnxnAcceptor cnxnAcceptor;
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

            }
        }catch (BindException e){
            if(self.getQuorumListOnAllIPs()){
                LOG.info("Couldn't bind to port " + self.getQuorumAddress().getPort(), e);
            }else{
                LOG.info("Couldn't bind to " + self.getQuorumAddress(), e);
            }
            throw e;
        }
        this.zk = zk;
    }







    class LearnerCnxnAcceptor extends Thread{

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
            }catch (SocketException e){
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

}
