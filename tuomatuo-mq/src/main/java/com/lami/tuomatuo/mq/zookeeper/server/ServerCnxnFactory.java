package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xujiankang on 2017/3/19.
 */
public abstract class ServerCnxnFactory {

    public static final String ZOOKEEPER_SERVER_CNXN_FACTORY = "zookeeper.serverCnxnFactory";

    private static final Logger LOG = LoggerFactory.getLogger(ServerCnxnFactory.class);

}
