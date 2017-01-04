package com.lami.tuomatuo.search.base.concurrent.executors.schedule;

import java.util.concurrent.Delayed;
import java.util.concurrent.Future;

/**
 * A delayed result-bearing action that can be cancelled.
 * Usually a scheduled future is the result of scheduling
 * a task with a {@link java.util.concurrent.ScheduledExecutorService}
 *
 * Created by xjk on 1/4/17.
 */
public interface ScheduledFuture<V> extends Delayed, Future<V> {
}
