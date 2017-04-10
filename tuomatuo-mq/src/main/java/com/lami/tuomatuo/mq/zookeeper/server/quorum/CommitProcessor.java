package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperCriticalThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This RequestProcessor matches the incoming committed requests with the
 * locally submitted requests. The trick is that locally submitted request that
 * change the state of the system will come back incoming committed requests
 * so we need to match them up
 *
 * Created by xujiankang on 2017/3/19.
 */
public class CommitProcessor extends Thread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CommitProcessor.class);

    // Request that we are holding until the commit comes in
    public LinkedList<Request> queuedRequests = new LinkedList<>();

    // Requests that have been committed
    public LinkedList<Request> committedRequests = new LinkedList<>();

    public RequestProcessor nextProcessor;
    public ArrayList<Request> toProcess = new ArrayList<>();

    /**
     * This flag indicates whether we need to wait for a response to come back from the
     * leader or we just let the sync operation flow through like a read. The flag will
     * to true if the CommitProcessor is in a Leader pipeline
     */
    public boolean matchSyncs;

    public CommitProcessor( RequestProcessor nextProcessor, String id, boolean matchSyncs) {
        super("CommitProcessor :" + id);
        this.nextProcessor = nextProcessor;
        this.matchSyncs = matchSyncs;
    }

    volatile  boolean finished = false;

    @Override
    public void run() {
        try{
            Request nextPending = null;

            while(!finished){
                int len = toProcess.size();
                for(int i = 0; i < len; i++){
                    nextProcessor.processRequest(toProcess.get(i));
                }

                toProcess.clear();
                synchronized (this){
                    if((queuedRequests.size() == 0 || nextPending != null)
                            && committedRequests.size() > 0){
                        Request r = committedRequests.remove();
                        /**
                         * We match with nextPending so that we can move to the
                         * next request when it is committed . We also want to
                         * use nextPending because it has the cnxn member set
                         * properly
                         */

                        if(nextPending != null
                                && nextPending.sessionId == r.sessionId
                                && nextPending.cxid == r.cxid){
                            // we want to send our version of the request
                            // the pointer to the connection in the request
                            nextPending.hdr = r.hdr;
                            nextPending.txn = r.txn;
                            nextPending.zxid = r.zxid;
                            toProcess.add(nextPending);
                            nextPending = null;
                        }
                        else{
                            // this is request came from someone also so just
                            // send the commit packet
                            toProcess.add(r);
                        }
                    }
                }

                // we haven't matched the pending requests, so go back to waiting
                if(nextPending != null){
                    continue;
                }

                synchronized (this){
                    // Process the next requests in the queueRequests
                    while(nextPending == null && queuedRequests.size() > 0){
                        Request request = queuedRequests.remove();
                        switch (request.type){
                            case ZooDefs.OpCode.create:
                            case ZooDefs.OpCode.delete:
                            case ZooDefs.OpCode.setData:
                            case ZooDefs.OpCode.multi:
                            case ZooDefs.OpCode.setACL:
                            case ZooDefs.OpCode.createSession:
                            case ZooDefs.OpCode.closeSession:
                                nextPending = request;
                                break;
                            case ZooDefs.OpCode.sync:{
                                if(matchSyncs){
                                    nextPending = request;
                                }
                                else{
                                    toProcess.add(request);
                                }
                            }
                            default:{
                                toProcess.add(request);
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            LOG.info("Interrupted exception while waiting ", e);
        }

        LOG.info("CommitProcessor exited loop!");
    }

    synchronized public void commit(Request request){
        if(!finished){
            if(request == null){
                LOG.info("Committed a null", new Exception("comitting a null"));
                return;
            }

            committedRequests.add(request);
            notifyAll();

        }
    }

    @Override
    synchronized public void processRequest(Request request) throws RequestProcessorException {
        LOG.info("Processing request : " + request);
        if(!finished){
            queuedRequests.add(request);
            notifyAll();
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Shuting down");
        synchronized (this){
            finished = true;
            queuedRequests.clear();
            notifyAll();
        }
        if(nextProcessor != null){
            nextProcessor.shutdown();
        }
    }
}
