package com.lami.tuomatuo.mq.netty.util.concurrent;

import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.TimeUnit;

/**
 *  A skeletal {@link Future} implementation which represents a {@link Future} which has been completed already
 *
 * Created by xjk on 12/14/16.
 */
public abstract class CompleteFuture<V> extends AbstractFuture<V>{

    private EventExecutor executor;

    /**
     * Create a new instance
     * @param executor {@link EventExecutor} associated with this future
     */
    protected CompleteFuture(EventExecutor executor) {
        this.executor = executor;
    }

    /**
     * Return the {@link EventExecutor} which is used by this {@link CompleteFuture}
     * @return
     */
    protected EventExecutor executor(){
        return executor;
    }


    @Override
    public Future<V> addlistener(GenericFutureListener<? extends Future<? super V>> listener) {
        if(listener == null){
            throw new NullPointerException("listener");
        }
//        DefaultPromise.notifyListener(executor(), this, listener);
        return this;
    }

    @Override
    public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        if(listeners == null){
            throw new NullPointerException("Listeners is null");
        }
        for(GenericFutureListener<? extends Future<? super V>> l : listeners){
            if(l == null){
                break;
            }
//            DefaultPromise.notifyListener(executor(), this, l);
        }

        return this;
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        return this;
    }

    @Override
    public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        return this;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return true;
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        return this;
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return true;
    }

    @Override
    public Future<V> awaitUninterruptibly() {
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return true;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return true;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
}
