package com.apache.catalina;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 3/6/17.
 */
public interface Executor extends java.util.concurrent.Executor, Lifecycle {

    String getName();

    /**
     * Executes the given command at some time in the future. The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the <tt>Executor</tt> implementation
     * If no threads are available, it will be added to the work queue
     * If the work queue is full, the system will wait for the specified
     * time until it throws a RejectedExecutionException
     *
     * @param command
     * @param timeout
     * @param unit
     */
    void execute(Runnable command, long timeout, TimeUnit unit);
}
