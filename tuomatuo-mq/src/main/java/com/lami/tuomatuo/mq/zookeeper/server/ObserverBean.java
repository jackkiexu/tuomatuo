package com.lami.tuomatuo.mq.zookeeper.server;


import com.lami.tuomatuo.mq.zookeeper.server.quorum.ObserverMXBean;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ObserverBean extends ZooKeeperServerBean implements ObserverMXBean {

    public ObserverBean(ZooKeeperServer zks) {
        super(zks);
    }

    @Override
    public int getPendingRevalidationCount() {
        return 0;
    }

    @Override
    public String getQuorumAddress() {
        return null;
    }
}
