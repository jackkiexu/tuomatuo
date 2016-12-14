package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Special {@link ProgressiveFuture} which os writable
 * Created by xjk on 12/14/16.
 */
public interface ProgressivePromise<V> extends Promise<V>, ProgressiveFuture<V> {

    /**
     * Sets the current progress of the operation and notifies the listener that implement
     * {@link io.netty.util.concurrent.GenericProgressiveFutureListener}
     * @param progress
     * @param total
     * @return
     */
    ProgressivePromise<V> setProgress(long progress, long total);

    /**
     * Tries to set the current progress of the operation and notifies the listeners that implement
     * {@link io.netty.util.concurrent.GenericProgressiveFutureListener}
     * @param progress
     * @param total
     * @return
     */
    boolean tryProgress(long progress, long total);

    @Override
    ProgressivePromise<V> setSuccess(V result);

    @Override
    ProgressivePromise<V> setFailure(Throwable cause);

    @Override
    ProgressivePromise<V> addlistener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressivePromise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressivePromise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressivePromise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressivePromise<V> await() throws InterruptedException;

    @Override
    ProgressivePromise<V> awaitUninterruptibly();

    @Override
    ProgressivePromise<V> sync() throws InterruptedException;

    @Override
    ProgressivePromise<V> syncUninterruptibly();
}
