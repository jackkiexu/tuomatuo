package com.lami.tuomatuo.mq.zookeeper.server.quorum;


import org.apache.zookeeper.server.quorum.QuorumPacket;

/**
 * This class has the control logic for the Follower
 * Created by xujiankang on 2017/3/19.
 */
public class Follower extends Learner{

    private long lastQueued;
    // this is the same object as this.zk but we cache the downcast op
    public FollowerZooKeeperServer fzk;

    public Follower(QuorumPeer self, FollowerZooKeeperServer zk) {
        this.self = self;
        this.zk = zk;
        this.fzk = zk;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Follower ").append(sock);
        sb.append(" lastQueuedZxid:").append(lastQueued);
        sb.append(" pendingRevalidationCount:")
                .append(pendingRevalidations.size());
        return sb.toString();
    }

    public void followLeader() throws Exception{

    }

    public void processPacket(QuorumPacket qp) throws Exception{

    }

    public long getZxid(){
        try{
            synchronized (fzk){
                return fzk.getZxid();
            }
        }catch (Exception e){
            LOG.info("error getting zxid", e);
        }
        return -1;
    }

    // Ths zxid of the last operation queued
    public long getLastQueued(){
        return lastQueued;
    }

    @Override
    public void shutdown() {
        LOG.info("shutdown called ", new Exception("shutdown Follower"));
        super.shutdown();
    }
}
