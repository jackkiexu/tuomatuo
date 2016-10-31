package com.lami.tuomatuo.mq.jafka.producer;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/20.
 */
public class SyncProducerConfig implements SyncProducerConfigShared {

    protected Properties props;

    int bufferSize;

    int connectTimeoutMs;

    int socketTimeoutMs;

    int reconnectInterval;

    int reconnectTimeInterval;

    int maxMessageSize;

    public Properties getProperties() {
        return null;
    }

    public int getBufferSize() {
        return 0;
    }

    public int getConnectTimeoutMs() {
        return 0;
    }

    public int getSocketTimeoutMs() {
        return 0;
    }

    public int getReconnectInterval() {
        return 0;
    }

    public int getReconnectTimeInterval() {
        return 0;
    }

    public int getMaxMessageSize() {
        return 0;
    }
}
