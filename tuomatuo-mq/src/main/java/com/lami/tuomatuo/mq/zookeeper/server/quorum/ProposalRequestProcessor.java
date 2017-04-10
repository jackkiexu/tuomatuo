package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;

/**
 * This RequestProcessor simply forwards requests to an AckRequestProcessor and
 * SyncRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ProposalRequestProcessor implements RequestProcessor {
    
    @Override
    public void processRequest(Request request) throws RequestProcessorException {

    }

    @Override
    public void shutdown() {

    }
}
