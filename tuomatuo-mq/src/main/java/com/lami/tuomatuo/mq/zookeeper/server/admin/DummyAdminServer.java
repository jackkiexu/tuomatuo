package com.lami.tuomatuo.mq.zookeeper.server.admin;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class DummyAdminServer implements AdminServer {
    @Override
    public void start() throws AdminServerException {

    }

    @Override
    public void shutdown() throws AdminServerException {

    }

    @Override
    public void setZooKeeperServer(ZooKeeperServer zkServer) {

    }
}
