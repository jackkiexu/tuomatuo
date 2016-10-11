package com.lami.tuomatuo.search.base.concurrent.unsafe.concurrency;

/**
 * Created by xjk on 2016/5/13.
 */
public interface Counter {
    void increment();
    long getCounter();
}
