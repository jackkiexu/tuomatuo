package com.lami.tuomatuo.mq.zookeeper.server;


import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
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
            while(!zks.outstandingChanges.isEmpty()
                    && zks.outstandingChanges.get(0).zxid <= request.zxid){
                ZooKeeperServer.ChangeRecord  cr = zks.outstandingChanges.remove(0);
                if(cr.zxid < request.zxid){
                    LOG.info("Zxid outstanding" + cr.zxid
                                + " is less than current " + request.zxid);
                }
                if(zks.outstandingChangesForPath.get(cr.path) == cr){
                    zks.outstandingChangesForPath.remove(cr.path);
                }
            }

            if(request.hdr != null){
                TxnHeader hdr = request.hdr;
                Record txn = request.txn;

                rc = zks.processTxn(hdr, txn);
            }

            // do not add non quorum packets to the queue
            if(Request.isQuorum(request.type)){
                zks.getZKDatabase().addCommittedProposal(request);
            }
        }
    }

    @Override
    public void shutdown() {
        LOG.info("shutdown of request processor complete");
    }
}
