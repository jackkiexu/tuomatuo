package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperCriticalThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;

/**
 * This RequestProcessor forwards any request that modify the state of the
 * system to the Leader
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FollowerRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor{
    public FollowerRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
