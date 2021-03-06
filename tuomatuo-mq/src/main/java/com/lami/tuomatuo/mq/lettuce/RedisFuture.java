package com.lami.tuomatuo.mq.lettuce;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/9/14.
 */
public interface RedisFuture<V> extends ListenableFuture<V> {
    String getError();

    boolean await(long timeout, TimeUnit unit);
}
