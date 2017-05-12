package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerMXBean;

/**
 *
 * Follower MBean
 * Created by xujiankang on 2017/3/19.
 */
public interface FollowerMXBean extends ZooKeeperServerMXBean{

    /**
     * socket address
     * @return
     */
    public String getQuorumAddress();

    /**
     * count of pending revalidation
     * @return
     */
    public int getPendingRevalidationCount();

    /**
     * time taken for leader election in milliseconds
     * @return
     */
    public long getElectionTimeTaken();
}
