package com.lami.tuomatuo.mq.jafka.producer.async;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/20.
 */
public interface AsyncProducerConfigShared {

    Properties getProperties();

    int getQueueTime();

    int getQueueSize();

    int getEnqueueTimeoutMs();

    int getBatchSize();

    String getSerializerClass();

    String getCbkHandler();

    Properties getCbkHandlerProperties();

    String getEventHandler();

    Properties getEventHandlerProperties();

}
