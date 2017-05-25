package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper server shutdown handler which will be used to handle ERROR or
 * SHUTDOWN server state transitions, which in turn release the associated
 * shutdown latch
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServerShutdownHandler {

    private final CountDownLatch shutdownLatch;

    public ZooKeeperServerShutdownHandler(CountDownLatch shutdownLatch) {
        this.shutdownLatch = shutdownLatch;
    }

    /**
     * This will be invoked when the server transition to a new server state
     * @param state
     */
    void handle(ZooKeeperServer.State state){
        if(state == ZooKeeperServer.State.ERROR || state == ZooKeeperServer.State.SHUTDOWN){
            shutdownLatch.countDown();
        }
    }
}
