package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import org.apache.zookeeper.server.quorum.QuorumPacket;

/**
 * Observers are peers that do not take part in the atomic broadcast protocal
 * Instead, they are informed of successful proposals by the Leader. Observers
 * therefore naturally act as a rely point for publishing the proposal stream
 * and can relieve Followers of some of the connection load. Observers may
 * submit proposals, but do not vote their acceptance
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Observer extends Learner {

    public Observer(QuorumPeer self, ObserverZooKeeperServer observerZooKeeperServer) {
        this.self = self;
        this.zk = observerZooKeeperServer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Observer ").append(sock);
        sb.append(" pendingRevalidationCount:")
                .append(pendingRevalidations.size());
        return sb.toString();
    }

    // the main method called by the observer to observer the leader
    public void observerLeader() throws Exception{

    }

    // Controls the response of an observer to the receipt of a quorumpacket
    public void processPacket(QuorumPacket qp) throws Exception{

    }

    @Override
    public void shutdown() {
        LOG.info("shutdown called ", new Exception("shutdown Observer"));
        super.shutdown();
    }
}
