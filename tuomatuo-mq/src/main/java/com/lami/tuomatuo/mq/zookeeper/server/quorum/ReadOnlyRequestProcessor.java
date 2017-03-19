package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperCriticalThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;

/**
 * This processor is at the begining of the ReadOnlyZooKeeperServer's
 * processors chain. All it does is, it passes read-only operations
 * through to the next processor, but drops
 * state-changing operation
 * Created by xujiankang on 2017/3/19.
 */
public class ReadOnlyRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor {
    public ReadOnlyRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
