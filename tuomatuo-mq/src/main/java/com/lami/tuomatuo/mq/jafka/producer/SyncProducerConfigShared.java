package com.lami.tuomatuo.mq.jafka.producer;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/19.
 */
public interface SyncProducerConfigShared {

    Properties getProperties();

    int getBufferSize();

    int getConnectTimeoutMs();

    int getSocketTimeoutMs();

    int getReconnectInterval();

    int getReconnectTimeInterval();

    int getMaxMessageSize();

}
