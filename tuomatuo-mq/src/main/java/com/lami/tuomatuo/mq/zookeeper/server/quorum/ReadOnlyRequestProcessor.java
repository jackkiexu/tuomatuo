package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.*;
import org.apache.zookeeper.proto.ReplyHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * This processor is at the begining of the ReadOnlyZooKeeperServer's
 * processors chain. All it does is, it passes read-only operations
 * through to the next processor, but drops
 * state-changing operation
 * Created by xujiankang on 2017/3/19.
 */
public class ReadOnlyRequestProcessor extends Thread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyRequestProcessor.class);

    private LinkedBlockingQueue<Request> queueRequests = new LinkedBlockingQueue<>();

    public boolean finished = false;

    private RequestProcessor nextProcessor;

    private ZooKeeperServer zks;

    public ReadOnlyRequestProcessor( ZooKeeperServer zks, RequestProcessor nextProcessor) {
        super("ReadOnlyRequestProcessor :" + zks.getServerId());
        this.nextProcessor = nextProcessor;
        this.zks = zks;
    }

    @Override
    public void run() {
        try{
            while (!finished){
                Request request = queueRequests.take();

                long traceMask = ZooTrace.CLIENT_REQUEST_TRACE_MASK;
                if(request.type == ZooDefs.OpCode.ping){
                    traceMask = ZooTrace.CLIENT_PING_TRACE_MASK;
                }

                if(Request.requestOfDeath == request){
                    break;
                }

                switch (request.type){
                    case ZooDefs.OpCode.sync:
                    case ZooDefs.OpCode.create:
                    case ZooDefs.OpCode.delete:
                    case ZooDefs.OpCode.setData:
                    case ZooDefs.OpCode.setACL:
                    case ZooDefs.OpCode.multi:
                    case ZooDefs.OpCode.check:{
                        ReplyHeader hdr = new ReplyHeader(request.cxid, zks.getZkDatabase().getDataTreeLastProcessedZxid(),
                                KeeperException.Code.NOTREADONLY.intValue());
                        try{
                            request.cnxn.sendResponse(hdr, null, null);
                        }catch (Exception e){
                            LOG.info("IO Exception while sending response", e);
                        }
                        continue;
                    }
                }

                // proceed to the next processor
                if(nextProcessor != null){
                    nextProcessor.processRequest(request);
                }

            }
        }catch (Exception e){
            LOG.error("Unexpected exception", e);
        }

        LOG.info("ReadOnlyRequestProcessor exited loop");
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        if(!finished){
            queueRequests.add(request);
        }
    }

    @Override
    public void shutdown() {
        finished = true;
        queueRequests.clear();
        queueRequests.add(Request.requestOfDeath);
        nextProcessor.shutdown();
    }
}
