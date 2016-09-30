package com.lami.tuomatuo.mq.jafka.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A scheduler for running jobs in the background
 * Created by xujiankang on 2016/9/30.
 */
public class Scheduler {

    private static final Logger logger = Logger.getLogger(Scheduler.class);

    AtomicLong threadId = new AtomicLong(0);

    ScheduledThreadPoolExecutor executor;

    String baseThreadName;


    public Scheduler(int numThreads, final String baseThreadName, final boolean isDaemon) {
        this.baseThreadName = baseThreadName;
        executor = new ScheduledThreadPoolExecutor(numThreads, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, baseThreadName + threadId.getAndIncrement());
                t.setDaemon(isDaemon);
                return t;
            }
        });
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    public ScheduledFuture<?> scheduleWithRate(Runnable command, long delayMs, long periodMs){
        return executor.scheduleWithFixedDelay(command, delayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    public void shutdownNow(){
        executor.shutdownNow();
        logger.info("shutdownNow scheduler " + baseThreadName);
    }

    public void shutdown(){
        executor.shutdown();
        logger.info("shutdown scheduler " + baseThreadName);
    }
}
