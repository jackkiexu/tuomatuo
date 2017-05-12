package com.lami.tuomatuo.mq.zookeeper.server.quorum;


import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class FollowerBean extends ZooKeeperServerBean implements FollowerMXBean{

    public FollowerBean(ZooKeeperServer zks) {
        super(zks);
    }

    @Override
    public String getQuorumAddress() {
        return null;
    }

    @Override
    public int getPendingRevalidationCount() {
        return 0;
    }

    @Override
    public long getElectionTimeTaken() {
        return 0;
    }
}
