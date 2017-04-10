package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Flushable;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This Requestprocessor logs requests to disk. It batches the requests to do
 * the io efficiently. The request is not passed to the next RequestProcessor
 * until its log has been synced to disk
 *
 * SyncRequestProcessor is used in 3 different cases
 * 1. Leader - Sync request to disk and forward it to AckRequestProcessor which
 *          send ack to itself
 * 2. Follower - Sync request t to disk and forward request to
 *          SendAckRequestProcessor which send the packets to leader
 *          SendAckRequestProcessor is flushable which allow us to force
 *          push packets to leader
 * 3. Observer - Sync committed request to disk (received as INFORM packet)
 *          It never send ack to the leader, so the nextProcessor will
 *          be null. This change the semantic of txnlog on the observer
 *          since it only contains committed txns
 *
 * Created by xjk on 3/19/17.
 */
public class SyncRequestProcessor extends Thread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SyncRequestProcessor.class);

    private final ZooKeeperServer zks;
    private final LinkedBlockingQueue<Request> queuedRequests = new LinkedBlockingQueue<>();
    private final RequestProcessor nextProcessor;

    private Thread snapInProcess = null;
    volatile private boolean running;

    /**Transaction that have been written and are waiting to be flushed to
     * disk Basically this is the list of SyncItems whose callbacks will be
     * invoked after flush returns successfully
     */
    private final LinkedList<Request> toFlush = new LinkedList<>();
    private final Random r = new Random(System.nanoTime());

    // The number of log entries to log before starting a snapshot
    private static int snapCount = ZooKeeperServer.getSnapCount();

    // The number of the log entries before rolling the log, number
    // is chosen randomly
    private static int randRoll;

    private final Request requestOfDeath = Request.requestOfDeath;

    public SyncRequestProcessor(ZooKeeperServer zks, RequestProcessor nextProcessor) {
        super("SyncThread:" + zks.getServerId());
        this.zks = zks;
        this.nextProcessor = nextProcessor;
        running = true;
    }

    // used by tests to check for changing snapcounts
    public static void setSnapCount(int count){
        snapCount = count;
        randRoll = count;
    }


    /**
     * used by tests to get the snapcount
     * @return the snapcount
     */
    public static int getSnapCount() {
        return snapCount;
    }

    /**
     * Sets the value of randRoll. This method
     * is here to avoid a findbugs warning for
     * setting a static variable in an instance
     * method.
     *
     * @param roll
     */
    private static void setRandRoll(int roll) {
        randRoll = roll;
    }

    @Override
    public void run() {
        try{
            int logCount = 0;

            /**
             * We do this in an attempt to ensure that not all of servers
             * in the enable take a snapshot at the smae time
             */
        setRandRoll(r.nextInt(snapCount / 2));
        while(true){
            Request si = null;
            if(toFlush.isEmpty()){
                si = queuedRequests.take();
            }
            else{
                si = queuedRequests.poll();
                if(si == null){
                    flush(toFlush);
                    continue;
                }
            }

            if(si == requestOfDeath){
                break;
            }

            if(si != null){
                // track the number of records written to the log
                if(zks.getZkDatabase().append(si)){
                    logCount++;
                    if(logCount > (snapCount / 2 + randRoll)){
                        randRoll = r.nextInt(snapCount / 2);
                        // roll the log
                        zks.getZkDatabase().rollLog();
                        // take a snapshot
                        if(snapInProcess != null && snapInProcess.isAlive()){
                            LOG.info("Too busy to snap, skipping");
                        }
                        else{
                            snapInProcess = new Thread("Snapshot Thread"){
                                @Override
                                public void run() {
                                    try{
                                        zks.takeSnapshot();
                                    }catch (Exception e){
                                        LOG.info("Unexpected expection", e);
                                    }
                                }
                            };

                            snapInProcess.start();
                        }
                        logCount = 0;
                    }
                }
                else if(toFlush.isEmpty()){
                    // optimization for read heavy workloads
                    // iff this is a read, and there are no pending
                    // flushes(writes), then just pass this to the next
                    // processor
                    if(nextProcessor != null){
                        nextProcessor.processRequest(si);
                        if(nextProcessor instanceof  Flushable){
                            ((Flushable)nextProcessor).flush();
                        }
                    }
                    continue;
                }

                toFlush.add(si);
                if(toFlush.size() > 1000){
                    flush(toFlush);
                }
            }
        }
        }catch (Throwable t){
            LOG.error("Serere unrecorverable error, existing", t);
            running = false;
            System.exit(11);
        }
        LOG.info("SystemRequestProcessor exited!");
    }


    private void flush(LinkedList<Request> toFlush) throws Exception{
        if(toFlush.isEmpty()){
            return;
        }

        zks.getZkDatabase().commit();
        while(!toFlush.isEmpty()){
            Request i = toFlush.remove();
            if(nextProcessor != null){
                nextProcessor.processRequest(i);
            }
        }

        if(nextProcessor != null && nextProcessor instanceof Flushable){
            ((Flushable)nextProcessor).flush();
        }
    }


    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        queuedRequests.add(requestOfDeath);
        try{
            if(running){
                this.join();
            }
            if(!toFlush.isEmpty()){
                flush(toFlush);
            }
        }catch (Exception e){
            LOG.info(e.getMessage(), e);
        }

        if(nextProcessor != null){
            nextProcessor.shutdown();
        }
    }


    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        queuedRequests.add(request);
    }

}
