package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * Listener for the critical resource events
 *
 * Created by xjk on 3/18/17.
 */
public interface ZooKeeperServerListener {

    /**
     * This will notify the server that some critical thread has stopped
     * It usually takes place when fatal error occurred.
     *
     * @param threadName
     * @param errorCode
     */
    void notifyStopping(String threadName, int errorCode);

}
