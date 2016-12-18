package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xjk on 12/18/16.
 */
public class DefaultThreadFactory implements ThreadFactory{

    private static final AtomicInteger poolId = new AtomicInteger();

    private final AtomicInteger nextId = new AtomicInteger();
    private String prefix;
    private boolean daemon;
    private int priority;
    private ThreadGroup threadGroup;

    public DefaultThreadFactory(Class<?> poolType) { }

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }

    private static final class DefaultRunnableDecorator implements Runnable{
        private Runnable r;

        DefaultRunnableDecorator(Runnable r) { this.r = r; }

        @Override
        public void run() {
            try {
                r.run();
            } finally {
                FastThreadLocal.removeAll();
            }
        }
    }
}
