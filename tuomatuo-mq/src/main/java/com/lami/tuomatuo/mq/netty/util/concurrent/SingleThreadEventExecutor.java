package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 12/18/16.
 */
public class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor{
    @Override
    public EventExecutorGroup parent() {
        return null;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return false;
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public Future<?> terminationFuture() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(Runnable command) {

    }
}
