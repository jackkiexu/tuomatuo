package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderBean extends ZooKeeperServerBean implements LeaderMXBean{
    public LeaderBean(ZooKeeperServer zks) {
        super(zks);
    }
}
