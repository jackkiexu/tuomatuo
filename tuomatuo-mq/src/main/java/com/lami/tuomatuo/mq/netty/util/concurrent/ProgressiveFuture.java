package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * A {@link Future} which is used to indicate the progress of an operation
 * Created by xjk on 12/14/16.
 */
public interface ProgressiveFuture<V> extends Future<V> {

    @Override
    ProgressiveFuture<V> addlistener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressiveFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    ProgressiveFuture<V> sync() throws InterruptedException;

    @Override
    ProgressiveFuture<V> syncUninterruptibly();

    @Override
    ProgressiveFuture<V> await() throws InterruptedException;

    @Override
    ProgressiveFuture<V> awaitUninterruptibly();
}
