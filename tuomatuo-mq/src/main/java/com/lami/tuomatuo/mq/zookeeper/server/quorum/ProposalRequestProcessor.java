package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.SyncRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This RequestProcessor simply forwards requests to an AckRequestProcessor and
 * SyncRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ProposalRequestProcessor implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ProposalRequestProcessor.class);

    public LeaderZooKeeperServer zks;

    public RequestProcessor nextProcessor;

    public SyncRequestProcessor syncProcessor;

    public ProposalRequestProcessor(LeaderZooKeeperServer zks, RequestProcessor nextProcessor) {
        this.zks = zks;
        this.nextProcessor = nextProcessor;
        AckRequestProcessor ackRequestProcessor = new AckRequestProcessor(zks.getLeader());
        syncProcessor = new SyncRequestProcessor(zks, ackRequestProcessor);
    }


    public void initialize(){
        syncProcessor.start();
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        if(request instanceof LearnerSyncRequest){
            zks.getLeader().processSync((LearnerSyncRequest)request);
        }
        else{
            nextProcessor.processRequest(request);

            if(request.hdr != null){
                // We need to sync and get consensus on any transactions
                try{
                    zks.getLeader().propose(request);
                }catch (Exception e){
                    throw new RequestProcessorException(e.getMessage(), e);
                }

                syncProcessor.processRequest(request);
            }
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        nextProcessor.shutdown();;
        syncProcessor.shutdown();
    }
}
