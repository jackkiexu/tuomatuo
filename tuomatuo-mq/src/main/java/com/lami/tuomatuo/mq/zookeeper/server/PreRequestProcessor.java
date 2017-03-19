package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * This request processor is genrally at the start of a RequestProcessor
 * change. It sets up any transaction associated with requests that change the
 * state of the system. It counts on ZooKeeperServer to update
 * outstandingRequests, so that it can take into account transactions that are
 * in the queue to be applied when generating a transaction
 *
 * Created by xujiankang on 2017/3/19.
 */
public class PreRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor {

    public PreRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }

}
