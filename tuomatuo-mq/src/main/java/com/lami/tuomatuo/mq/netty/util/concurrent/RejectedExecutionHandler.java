package com.lami.tuomatuo.mq.netty.util.concurrent;


/**
 * Similar to {@link java.util.concurrent.RejectedExecutionHandler} but specific to {@link io.netty.util.concurrent.SingleThreadEventExecutor}
 * Created by xujiankang on 2016/12/14.
 */
public interface RejectedExecutionHandler {

    /**
     * Called when someone tried to add a task to {@link SingleThreadEventExecutor} but this failed due capacity
     * @param task
     * @param executor
     */
    void reject(Runnable task, SingleThreadEventExecutor executor);
}
