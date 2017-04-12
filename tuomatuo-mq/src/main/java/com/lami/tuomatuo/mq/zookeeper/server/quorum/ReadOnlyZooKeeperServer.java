package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;

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

    private volatile boolean shutdown = false;

    public ReadOnlyZooKeeperServer(FileTxnSnapLog txnLogFactory, QuorumPeer self, DataTreeBuilder treeBuilder, ZKDatabase zkDb) {
        super(txnLogFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb);
    }

    @Override
    protected void setupRequestProcessors() {

    }

    @Override
    public void startup() {

    }

    @Override
    protected void registerJMX() {

    }

    protected void registerJMX(ZooKeeperServerBean serverBean, LocalPeerBean localPeerBean) {

    }

    @Override
    protected void unregisterJMX() {

    }

    protected void unregisterJMX(ZooKeeperServer zks) {

    }

    @Override
    public String getState() {
        return "read-only";
    }

    @Override
    public long getServerId() {
        return self.getid();
    }


    @Override
    public void shutdown() {

    }
}
