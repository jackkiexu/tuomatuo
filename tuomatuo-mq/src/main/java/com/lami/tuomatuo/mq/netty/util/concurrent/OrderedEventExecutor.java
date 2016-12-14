package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Marker interface for {@link EventExecutor}'s that will process all submitted tasks in an ordered / serial fashion
 *
 * Created by xjk on 12/14/16.
 */
public interface OrderedEventExecutor extends EventExecutor {
}
