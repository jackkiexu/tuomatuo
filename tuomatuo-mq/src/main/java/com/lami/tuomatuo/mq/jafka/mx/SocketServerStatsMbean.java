package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/9/30.
 */
public interface SocketServerStatsMbean {

    double getProduceRequestsPerSecond();

    double getFetchRequestsPerSecond();

    double getAvgProduceRequestMs();

    double getMaxProduceRequestsMs();

    double getAvgFetchRequestsMs();

    double getMaxFetchRequestsMs();

    double getBytesReadPerSecond();

    double getBytesWrittenPerSecond();

    long getNumFetchRequests();

    long getNumProduceRequests();

    long getTotalByteRead();

    long getTotalBytesWritten();

    long getTotalFetchRequestMs();

    long getTotalProduceRequestMs();

}
