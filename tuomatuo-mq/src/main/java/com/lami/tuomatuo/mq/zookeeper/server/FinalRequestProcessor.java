package com.lami.tuomatuo.mq.zookeeper.server;


import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Request processor actually applies transaction associated with a
 * request and services any quiries It is always at the end of a
 * RequestProcessor chain (hence the name). so it does not have a nextProcessor
 * member
 *
 * This RequestProcessor counts on ZooKeeperServer to populate the
 * outstandingRequests member of ZooKeeperServer
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FinalRequestProcessor implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FinalRequestProcessor.class);

    ZooKeeperServer zks;

    public FinalRequestProcessor(ZooKeeperServer zks) {
        this.zks = zks;
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        LOG.info("Processing request :" + request);
        long traceMask = ZooTrace.CLIENT_REQUEST_TRACE_MASK;
        if(request.type == ZooDefs.OpCode.ping){
            traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
        }
        ZooTrace.logRequest(LOG, traceMask, 'E', request, "");
        DataTree.ProcessTxnResult rc = null;


        synchronized (zks.outstandingChanges){

        }
    }

    @Override
    public void shutdown() {
        LOG.info("shutdown of request processor complete");
    }
}
