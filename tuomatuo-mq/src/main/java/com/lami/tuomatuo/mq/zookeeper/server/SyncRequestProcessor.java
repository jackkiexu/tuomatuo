package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * Created by xjk on 3/19/17.
 */
public class SyncRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor {

    public SyncRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }
}
