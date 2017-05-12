package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents critical thread. When there is an uncaught exception thrown by the
 * thread this will exit the system
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperCriticalThread extends ZooKeeperThread {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperCriticalThread.class);

    private final ZooKeeperServerListener listener;

    public ZooKeeperCriticalThread(String name, ZooKeeperServerListener listener) {
        super(name);
        this.listener = listener;
    }


    @Override
    protected void handleException(String thName, Throwable e) {
        LOG.error("Severe unrecoverable error, from thread: {}", thName, e);
        listener.notifyStopping(thName, ExitCode.UNEXPECTED_ERROR);
    }
}
