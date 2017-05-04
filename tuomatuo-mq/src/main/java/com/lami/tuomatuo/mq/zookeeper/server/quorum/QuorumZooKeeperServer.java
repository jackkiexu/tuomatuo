package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;

import java.io.PrintWriter;

/**
 * Abstract base class for all ZooKeeperServers that participate in
 * a quorum
 * Created by xujiankang on 2017/3/19.
 */
public abstract class QuorumZooKeeperServer extends ZooKeeperServer {

    public final QuorumPeer self;

    public QuorumZooKeeperServer(FileTxnSnapLog txnLogFactory, int tickTime,
                                 int minSessionTimeout, int maxSessionTimeout,
                                 DataTreeBuilder treeBuilder, ZKDatabase zkDb, QuorumPeer self) {
        super(txnLogFactory, tickTime, minSessionTimeout, maxSessionTimeout, treeBuilder, zkDb);
        this.self = self;
    }

    @Override
    public void dumpConf(PrintWriter pwriter) {
        super.dumpConf(pwriter);

        pwriter.print("initLimit=");
        pwriter.print(self.getInitLimit());
        pwriter.print("syncLimit=");
        pwriter.print(self.getSyncLimit());
        pwriter.print("electionAlg=");
        pwriter.print(self.getElectionType());
        pwriter.print("electionPort=");
        pwriter.print(self.quorumPeers.get(self.getId()).electionAddr.getPort());
        pwriter.print("quorumPort=");
        pwriter.print(self.quorumPeers.get(self.getId()).addr.getPort());
        pwriter.print("peerType=");
        pwriter.print(self.getLearnerType().ordinal());
    }
}
