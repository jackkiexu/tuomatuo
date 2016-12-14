package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * The result of an scheduled asynchrounous operation
 * Created by xujiankang on 2016/12/14.
 */
public interface ScheduledFuture<V> extends Future<V>, java.util.concurrent.ScheduledFuture<V> {
}
