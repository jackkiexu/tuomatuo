package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maintains the in memory database of zookeeper
 * server states that includes the sessions, datatree and the
 * committed logs. It is booted up after reading the logs
 * and snapshots from the disk
 *
 *
 * Created by xjk on 3/18/17.
 */
public class ZKDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(ZKDatabase.class);

}
