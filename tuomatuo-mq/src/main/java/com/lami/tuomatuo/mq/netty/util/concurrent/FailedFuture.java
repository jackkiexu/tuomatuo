package com.lami.tuomatuo.mq.netty.util.concurrent;

import com.lami.tuomatuo.mq.netty.util.internal.PlatformDependent;

/**
 * The {@link CompleteFuture} which is failed already. It is
 * recommended to use {@link EventExecutor#newFailedFuture(Throwable)}
 * instead of calling the constructor of this future
 *
 * Created by xjk on 12/15/16.
 */
public final class FailedFuture<V> extends CompleteFuture<V> {

    private Throwable cause;

    /**
     * Create a new instance
     *
     * @param executor {@link EventExecutor} associated with this future
     * @param cause the cause of the failure
     */
    protected FailedFuture(EventExecutor executor, Throwable cause) {
        super(executor);
        if(cause == null){
            throw new NullPointerException("cause");
        }
        this.cause = cause;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public Future<V> sync() throws InterruptedException {
//        PlatformDependent.throwException(cause);
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
//        PlatformDependent.throwException(cause);
        return this;
    }

    @Override
    public V getNow() {
        return null;
    }
}
