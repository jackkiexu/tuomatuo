package com.apache.tomcat.util.threads;


import java.util.concurrent.Executor;

/**
 * Created by xjk on 3/9/17.
 */
public interface ResizableExecutor extends Executor {


    /**
     * Returns the current number of threads in the pool
     * @return the number of threads
     */
    int getPoolSize();

    int getMaxThreads();

    /**
     * Returns the approximate number of threads that are actively executing
     * tasks
     * @return the number of threads
     */
    int getActiveCount();

    boolean resizePool(int corePoolSize, int maximumPoolSize);

    boolean resizeQueue(int capacity);

}
