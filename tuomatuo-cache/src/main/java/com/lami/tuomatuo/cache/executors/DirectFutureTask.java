package com.lami.tuomatuo.cache.executors;

import java.util.concurrent.*;

/**
 * Created by xjk on 2016/12/6.
 */
public class DirectFutureTask<V> extends FutureTask<V> {

    public DirectFutureTask(Callable<V> callable) {
        super(callable);
    }

    public DirectFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        super.run();
        return super.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        super.run();
        return super.get(timeout, unit);
    }
}
