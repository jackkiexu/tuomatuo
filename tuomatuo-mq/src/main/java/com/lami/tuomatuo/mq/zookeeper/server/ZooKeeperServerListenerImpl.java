package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * Default listener implementation, which will be used to notify internal
 * errors. For example, if some critical thread has stopped due to fatal errors,
 * then it will get notifications and will change the state of ZooKeeper server
 * to ERROR representing an error status
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServerListenerImpl implements ZooKeeperServerListener {
    @Override
    public void notifyStopping(String threadName, int errorCode) {

    }
}
