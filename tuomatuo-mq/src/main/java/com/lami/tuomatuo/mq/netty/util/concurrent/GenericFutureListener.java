package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.EventListener;

/**
 * Listener to the result of a {@link Future}. The result of the asynchronous operation is notified once this listener
 * is added by calling {@link Future#addListener(GenericFutureListener)}
 * Created by xujiankang on 2016/12/14.
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    /**
     *  Invoked when the operation associated with the {@link Future} has been completed
     * @param future the source {@link Future} which called this callback
     */
    void operationComplete(F future) throws Exception;
}
