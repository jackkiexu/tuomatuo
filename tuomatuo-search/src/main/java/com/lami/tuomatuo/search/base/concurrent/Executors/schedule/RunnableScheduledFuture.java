package com.lami.tuomatuo.search.base.concurrent.Executors.schedule;

import java.util.concurrent.RunnableFuture;

/**
 * A {@link ScheduledFuture} that is {@link Runnable}. Successful
 * execution of the {@code run} method causes completion of the
 * {@code Future} and allows access to its results
 *
 * Created by xjk on 1/4/17.
 */
public interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {

    /**
     * Returns {@code true} if this task is periodic. A periodic task may
     * re-run according to some schedule. A non-periodic task can be
     * run only once
     *
     * @return {@code true} if this task is periodic
     */
    boolean isPeriodic();
}
