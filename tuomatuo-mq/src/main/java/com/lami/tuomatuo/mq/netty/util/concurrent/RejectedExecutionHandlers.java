package com.lami.tuomatuo.mq.netty.util.concurrent;

import io.netty.util.concurrent.*;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Expose helper methods which create different {@link RejectedExecutionHandler}s
 * Created by xjk on 12/18/16.
 */
public class RejectedExecutionHandlers {

    private static final RejectedExecutionHandler REJECT = new RejectedExecutionHandler() {
        @Override
        public void reject(Runnable task, SingleThreadEventExecutor executor) {
            throw new RejectedExecutionException();
        }
    };

    public RejectedExecutionHandlers() {}

    /**
     * Returns a {@link RejectedExecutionHandler} that will always just throws a {@link RejectedExecutionHandler}
     * @return
     */
    public static RejectedExecutionHandler reject() { return REJECT; }

    public static RejectedExecutionHandler backoff(final int retries, long backoffAmount, TimeUnit unit){
        if(retries <= 0){
            throw new IllegalArgumentException(retries + " : " + retries + " (expected: > 0)");
        }
        final long backOffNanos = unit.toNanos(backoffAmount);
        return ((task, executor) -> {
            if(!executor.inEventLoop()){
                for(int i = 0; i < retries; i++){
                    // Try to wake the executor so it will empty its task queue
                    executor.wakeup(false);

                    LockSupport.parkNanos(backOffNanos);
                    if(executor.offerTask(task)){
                        return;
                    }
                }
            }

            /**
             * Either we tried to add that task from within the EventLoop or we was not able to add it even with
             * backoff
             */
            throw new RejectedExecutionException();
        });
    }
}
