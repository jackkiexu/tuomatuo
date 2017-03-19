package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;

/**
 * There will be an interface of this class created by the Leader for each
 * leaner, All communication with a learner is handled by this
 * class
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LearnerHandler extends ZooKeeperThread {
    public LearnerHandler(String name) {
        super(name);
    }
}
