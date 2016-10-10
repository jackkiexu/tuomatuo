package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/10/10.
 */
public interface LogFlushStatsMBean {

    double getFlushesPerSecond();

    double getAvgFlushMs();

    long getTotalFlushMs();

    double getMaxFlushMs();

    long getNumFlushes();
}
