package com.lami.tuomatuo.mq.netty.util.concurrent;


import java.util.concurrent.ThreadFactory;

/**
 * Created by xjk on 12/18/16.
 */
public class DefaultEventExecutor extends SingleThreadEventExecutor {
    protected DefaultEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super(parent, threadFactory, addTaskWakesUp, maxPendingTasks, rejectedHandler);
    }

    @Override
    protected void run() {

    }
}
