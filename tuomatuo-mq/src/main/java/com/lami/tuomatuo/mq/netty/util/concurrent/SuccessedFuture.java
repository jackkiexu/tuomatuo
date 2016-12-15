package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * The {@link CompleteFuture} which is successed already. It is
 * recommended to use {@link EventExecutor#newSuccessedFuture(Object)} instead of
 * calling the constructor of this future
 *
 * Created by xjk on 12/15/16.
 */
public final class SuccessedFuture<V> extends CompleteFuture<V> {

    private V result;

    /**
     * Create a new instance
     *
     * @param executor {@link EventExecutor} associated with this future
     */
    protected SuccessedFuture(EventExecutor executor, V result) {
        super(executor);
        this.result = result;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public V getNow() {
        return result;
    }
}
