package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.SyncRequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * just like the standard ZooKeeperServer. We just replace the request
 * processors: FollowerRequestProcessor -> CommitProcessor ->
 * FinalRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FollowerZooKeeperServer extends LearnerZooKeeperServer {

    private static final Logger LOG = LoggerFactory.getLogger(FollowerZooKeeperServer.class);

    public CommitProcessor commitProcessor;
    public SyncRequestProcessor syncProcessor;

    // Pending sync requests
    public ConcurrentLinkedQueue<Request> pendingSyncs;

    public FollowerZooKeeperServer(FileTxnSnapLog txnLogFactory, QuorumPeer self,  DataTreeBuilder treeBuilder, ZKDatabase zkDb) {
        super(txnLogFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
        this.pendingSyncs = new ConcurrentLinkedQueue<Request>();
    }

    public Follower getFollower(){
        return self.follower;
    }


    @Override
    protected void setupRequestProcessors() {

    }

    public LinkedBlockingQueue<Request> pendingTxns = new LinkedBlockingQueue<>();


    public void logRequest(TxnHeader hdr, Record txn){

    }

    /**
     * When a COMMIT message is received, eventually this method is called,
     * which matches up the zxid from the COMMIT with (hopefully the head of
     * the pendingTxns queue and hands it to the commitProcessor to commit
     * @param zxid
     */
    public void commit(long zxid){

    }


    public synchronized void sync(){

    }

    public int getGlobalOutstandingLimit(){
        return 0;
    }


    @Override
    public void shutdown() {

    }

    @Override
    public String getState() {
        return "follower";
    }

    @Override
    public Learner getLearner() {
        return null;
    }
}
