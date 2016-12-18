package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Created by xjk on 12/18/16.
 */
public class PromiseAggregator<V, F extends Future<V>> implements GenericFutureListener<F> {
    @Override
    public void operationComplete(F future) throws Exception {

    }
}
