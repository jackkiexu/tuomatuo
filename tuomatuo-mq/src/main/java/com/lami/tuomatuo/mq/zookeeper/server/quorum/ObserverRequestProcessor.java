package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperCriticalThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * This RequestProcessor forwards any request that modify the state of the
 * system to the Leader
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ObserverRequestProcessor extends Thread implements RequestProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(ObserverRequestProcessor.class);

    public ObserverZooKeeperServer zks;
    public RequestProcessor nextProcessor;
    /**
     * We keep a queue of requests. As requests get submitted they are
     * stored here. the queue is drained in the run() method
    */
    public LinkedBlockingQueue<Request> queuedRequests = new LinkedBlockingQueue<>();

    public boolean finished = false;

    public ObserverRequestProcessor(ObserverZooKeeperServer zks, RequestProcessor nextProcessor) {
        super("ObserverRequestProcessor:" + zks.getServerId());
        this.zks = zks;
        this.nextProcessor = nextProcessor;
    }


    @Override
    public void run() {
        try{
            while(!finished){
                Request request = queuedRequests.take();

                if(request == Request.requestOfDeath){
                    break;
                }

                /**
                 * We want to queue the request to be processed before we shumit
                 * the request to the leader so that ready to receive
                 * the response
                 */
                nextProcessor.processRequest(request);

                /**
                 * We now ship the request to the leader. As with all
                 * other quorum operation, sync also follows this code
                 * path, but different from others, we need to keep track
                 * of the sync operations this Observer has pending, so we
                 * add it to pending Sync
                 */
                switch (request.type){
                    case ZooDefs.OpCode.sync:{
                        zks.pendingSyncs.add(request);
                        zks.getObserver().request(request);
                        break;
                    }
                    case ZooDefs.OpCode.create:
                    case ZooDefs.OpCode.delete:
                    case ZooDefs.OpCode.setData:
                    case ZooDefs.OpCode.setACL:
                    case ZooDefs.OpCode.createSession:
                    case ZooDefs.OpCode.closeSession:
                    case ZooDefs.OpCode.multi:{
                        zks.getObserver().request(request);
                        break;
                    }
                }
            }
        }catch (Exception e){
            LOG.error("Unexpected exception causing exit", e);
        }
        LOG.info("ObserverrequestProcessor exited loop");
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        if(!finished){
            queuedRequests.add(request);
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        finished = true;
        queuedRequests.clear();
        queuedRequests.add(Request.requestOfDeath);
        nextProcessor.shutdown();
    }
}
