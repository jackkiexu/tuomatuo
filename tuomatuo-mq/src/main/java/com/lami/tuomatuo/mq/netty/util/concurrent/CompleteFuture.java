package com.lami.tuomatuo.mq.netty.util.concurrent;

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

        return null;
    }
}
