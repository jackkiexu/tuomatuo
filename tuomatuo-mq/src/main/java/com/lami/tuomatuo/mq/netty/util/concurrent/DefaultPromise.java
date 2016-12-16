package com.lami.tuomatuo.mq.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 12/16/16.
 */
public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public Future<V> addlistener(GenericFutureListener<? extends Future<? super V>> listener) {
        return null;
    }

    @Override
    public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        return null;
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return null;
    }

    @Override
    public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        return null;
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        return null;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        return null;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        return null;
    }

    @Override
    public Future<V> awaitUninterruptibly() {
        return null;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return false;
    }

    @Override
    public V getNow() {
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        return null;
    }

    @Override
    public boolean trySuccess(V result) {
        return false;
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        return null;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return false;
    }

    @Override
    public boolean setUncancellable() {
        return false;
    }
}
