package com.lami.tuomatuo.mq.zookeeper.server;

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
public class SyncRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor {

    public SyncRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
