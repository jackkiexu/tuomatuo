package com.lami.tuomatuo.search.base.concurrent.scheduledthreadpoolexecutor;

import com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KThreadPoolExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2017/1/12.
 */
public class KScheduledThreadPoolExecutor extends KThreadPoolExecutor implements KScheduledExecutorService {

    @Override
    public KScheduledFuture<?> submit(Runnable command, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public <V> KScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public KScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return null;
    }

    @Override
    public KScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public boolean isterminated() {
        return false;
    }
}
