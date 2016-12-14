package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Created by xjk on 12/14/16.
 */
public interface GenericProgressiveFutureListener<F extends ProgressiveFuture<?>> extends GenericFutureListener {

    /**
     * Invoked when the operation has progressed
     * @param future
     * @param progress the progress of the operation so far (cumulative)
     * @param total    the number that signifies the end of the operation when {@code progress} reaches at it
     *                 {@code -1} the end of the operation is unknow
     * @throws Exception
     */
    void operationProgressed(F future, long progress, long total) throws Exception;
}
