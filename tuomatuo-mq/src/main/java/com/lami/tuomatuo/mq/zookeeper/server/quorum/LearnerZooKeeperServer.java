package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxn;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;

import java.util.HashMap;

/**
 * Parent class for all ZooKeeperServers for Learners
 * Created by xujiankang on 2017/3/19.
 */
public abstract class LearnerZooKeeperServer extends QuorumZooKeeperServer{

    public LearnerZooKeeperServer(FileTxnSnapLog txnLogFactory, int tickTime,
                                  int minSessionTimeout, int maxSessionTimeout,
                                  DataTreeBuilder treeBuilder, ZKDatabase zkDb, QuorumPeer self) {
        super(txnLogFactory, tickTime, minSessionTimeout, maxSessionTimeout, treeBuilder, zkDb, self);
    }

    /**
     * Abstract method to return the leader associated with this server
     * Since the learner may change under our feet (when QuorumPeer reassigns
     * it) we can't simply take a reference here. Instead, we need the
     * subclasses to implement this
     */
    abstract public Learner getLearner();

    /**
     * Return the current state of the session tracker. This is only currently
     * used by a Learner to build a ping response packet
     * @return
     */
    public HashMap<Long, Integer> getTouchSnapshot(){
        if(sessionTracker != null){
            return ((LearnerSessionTracker)sessionTracker).snapshot();
        }
        return new HashMap<>();
    }


    @Override
    public long getServerId() {
        return self.getId();
    }

    @Override
    protected void createSessionTracker() {

    }

    @Override
    public void startSessionTracker() {

    }

    @Override
    public void revalidateSession(ServerCnxn cnxn, long sessionId, int sessionTimeout) throws Exception {

    }

    @Override
    protected void registerJMX() {

    }

    protected void registerJMX(ZooKeeperServerBean serverBean, LocalPeerBean localPeerBean) {

    }

    @Override
    protected void unregisterJMX() {

    }

    protected void unregisterJMX(Learner learner) {

    }
}
