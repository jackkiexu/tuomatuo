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
public class FollowerRequestProcessor extends Thread implements RequestProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(FollowerRequestProcessor.class);

    public FollowerZooKeeperServer zks;

    public RequestProcessor nextProcessor;

    public LinkedBlockingQueue<Request> queueRequests = new LinkedBlockingQueue<>();

    public boolean finished = false;

    public FollowerRequestProcessor(FollowerZooKeeperServer zks, RequestProcessor nextProcessor) {
        super("FollowerRequestProcessor : " + zks.getServerId());
        this.zks = zks;
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
        try{
            while(!finished){
                Request request = queueRequests.take();

                if(request == Request.requestOfDeath){
                    break;
                }

                /**
                 * We want to queue the request to be processed before we submit
                 * the request to the leader so that we are ready to receive
                 * the response
                 */
                nextProcessor.processRequest(request);

                /**
                 * we now ship the request to the leader, As with all
                 * other quorum operations, sync also follows this code
                 * path, but different from others, we need to keep track
                 * of the sync operations this follower has pending, so we
                 * add it to pendingSyncs
                 */

                switch (request.type){
                    case ZooDefs.OpCode.sync:{
                        zks.pendingSyncs.add(request);
                        zks.getFollower().request(request);
                        break;
                    }
                    case ZooDefs.OpCode.create:
                    case ZooDefs.OpCode.delete:
                    case ZooDefs.OpCode.setData:
                    case ZooDefs.OpCode.setACL:
                    case ZooDefs.OpCode.createSession:
                    case ZooDefs.OpCode.closeSession:
                    case ZooDefs.OpCode.multi:{
                        zks.getFollower().request(request);
                        break;
                    }
                }
            }
        }catch (Exception e){
            LOG.info("Unexpected exception causing exit", e);
        }

        LOG.info("FollowerRequestProcessor exited loop!");
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        if(!finished){
            queueRequests.add(request);
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        finished = true;
        queueRequests.clear();
        queueRequests.add(Request.requestOfDeath);
        nextProcessor.shutdown();
    }
}
