package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;

/**
 * A ZooKeeperServer which comes into play when peer is partitioned from the
 * majority. Handles read-only clients, but drops connections from not-read-only
 * ones
 * The very first processor in the chain of request processors is a
 * ReadOnlyRequestProcessor which clients, but drops connections from not-read-only
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ReadOnlyZooKeeperServer extends ZooKeeperServer {
}
