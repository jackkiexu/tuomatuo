package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/10/9.
 */
public interface SyncProducerStatsMBean {

    double getProduceRequestsPerSecond();

    double getAvgProduceRequestms();

    double getMaxProduceRequestMs();

    long getNumProduceRequests();
}
