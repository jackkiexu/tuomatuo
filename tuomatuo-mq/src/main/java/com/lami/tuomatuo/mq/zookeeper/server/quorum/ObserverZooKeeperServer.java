package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.SyncRequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * A ZooKeeperServer for the Observer node Type. Not much is different, but
 * we anticipate specializing the request processors in the future
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ObserverZooKeeperServer extends LearnerZooKeeperServer {

    private static final Logger LOG = LoggerFactory.getLogger(ObserverZooKeeperServer.class);

    /**
     * Enable since request processor for writting txnlog to disk and
     * take periodic snapshot Default is ON
     */
    private boolean syncRequestProcessorEnabled = this.self.getSyncEnabled();

    public ObserverZooKeeperServer(FileTxnSnapLog txnLogFactory, QuorumPeer self, DataTreeBuilder treeBuilder, ZKDatabase zkDb) {
        super(txnLogFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
    }

    // Request processor
    public CommitProcessor commitProcessor;
    public SyncRequestProcessor syncProcessor;

    // pending sync requests
    public ConcurrentLinkedQueue<Request> pendingSyncs = new ConcurrentLinkedQueue<>();


    public Learner getLearner(){
        return self.observer;
    }

    public Observer getObserver(){
        return self.observer;
    }

    /**
     *
     * @param request
     */
    public void commitRequest(Request request){

    }

    @Override
    protected void setupRequestProcessors() {

    }

    // Process a syn request
    synchronized public void sync(){
        if(pendingSyncs.size() == 0){
            LOG.warn("Not expecting a sync");
            return;
        }

        Request r = pendingSyncs.remove();
        commitProcessor.commit(r);
    }

    @Override
    public String getState() {
        return "observer";
    }

    @Override
    public void shutdown() {
        super.shutdown();

    }
}
