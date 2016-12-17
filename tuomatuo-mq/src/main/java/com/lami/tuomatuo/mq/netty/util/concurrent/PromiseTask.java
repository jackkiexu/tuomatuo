package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * Created by xjk on 12/17/16.
 */
public class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {

    public PromiseTask(EventExecutor executor, Runnable runnable, V result) {
        super(executor);
    }

    public PromiseTask(EventExecutor executor, Callable<V> callable) {
        super(executor);
    }

    @Override
    public void run() {

    }
}
