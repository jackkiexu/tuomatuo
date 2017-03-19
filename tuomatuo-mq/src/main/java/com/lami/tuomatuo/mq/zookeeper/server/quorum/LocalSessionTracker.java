package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.SessionTrackerImpl;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class LocalSessionTracker extends SessionTrackerImpl {
    public LocalSessionTracker(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
