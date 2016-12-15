package com.lami.tuomatuo.mq.netty.util.internal;

/**
 * Counter for long
 * Created by xjk on 12/15/16.
 */
public interface LongCounter {

    void add(long delta);

    void increment();

    void decrement();

    long value();

}
