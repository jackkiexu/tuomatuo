package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main class for catching all the uncaught exceptions thrown by the
 * threads
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperThread.class);

    private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            handleException(t.getName(), e);
        }
    };

    public ZooKeeperThread(String name) {
        super(name);
        setUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    /**
     * This will be used by the uncaught exception andler and just log a
     * warning message and return
     * @param thName
     * @param e
     */
    protected void handleException(String thName, Throwable e){
        LOG.warn("Exception occurred from thread {}", thName, e);
    }
}
