package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperCriticalThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class CommitProcessor extends ZooKeeperCriticalThread implements RequestProcessor {
    public CommitProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
