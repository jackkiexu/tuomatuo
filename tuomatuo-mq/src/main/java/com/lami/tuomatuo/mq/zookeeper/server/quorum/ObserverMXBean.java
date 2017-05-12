package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerMXBean;

/**
 * Observer MX Bean Interface, implemented by Observerbean
 * Created by xujiankang on 2017/3/19.
 */
public interface ObserverMXBean extends ZooKeeperServerMXBean {

    /**
     * count of pending revalidations
     * @return
     */
    public int getPendingRevalidationCount();


    /**
     * socket address
     * @return
     */
    public String  getQuorumAddress();
}
