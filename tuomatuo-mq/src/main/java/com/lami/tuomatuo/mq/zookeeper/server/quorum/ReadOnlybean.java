package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;

/**
 * ReadOnly XMBean interface, implemented by ReadOnlyBean
 * Created by xujiankang on 2017/3/19.
 */
public class ReadOnlybean extends ZooKeeperServerBean {

    public ReadOnlybean(ZooKeeperServer zks) {
        super(zks);
    }

    public String getname(){
        return "ReadOnlyServer";
    }

}
