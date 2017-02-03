package com.lami.tuomatuo.search.base.concurrent.scheduledthreadpoolexecutor;

import com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KThreadPoolExecutor;

/**
 * A handler for tasks that cannot be executed by a {@link com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KThreadPoolExecutor}
 *
 * Created by xujiankang on 2017/2/3.
 */
public interface KRejectedExecutionHandler {

    /**
     * Method that may be invoked by a {@link KThreadPoolExecutor} when
     * {@link KThreadPoolExecutor#execute(Runnable)} cannot accept a
     * task. This may occur when no more threads or queue slots are
     * available because their bounds would ba exceeded, or upon
     * shutdown of the Executor
     *
     * <p>
     *     In the absence of other alternatives, the method may throw
     *     an unchecked {@link KRejectedExecutionHandler}, which will be
     *     propagated to the caller of {@code execute}
     * </p>
     *
     * @param a the runnable task requested to be executed
     * @param executor the ececutor attempting to executes this task
     * @throws java.util.concurrent.RejectedExecutionException if there is no ready
     */
    void rejectedExecution(Runnable a, KThreadPoolExecutor executor);
}
