package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.utils.Utils;

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

    public SyncProducerConfig(Properties props) {
        this.props = props;
        this.bufferSize = Utils.getInt(props, "buffer.size", 100 * 1024);
        this.connectTimeoutMs = Utils.getInt(props, "connect.timeout.ms", 5000);
        this.socketTimeoutMs = Utils.getInt(props, "socket.timeout.ms", 30000);
        this.reconnectInterval = Utils.getInt(props, "reconnect.interval", 30000);
        this.reconnectTimeInterval = Utils.getInt(props, "reconnect.time.interval.ms", 1000 * 1000 * 10);
        this.maxMessageSize = Utils.getInt(props, "max.message.size", 1000 * 1000); // 1MB
    }


    public String getHost(){
        return Utils.getString(props, "host");
    }

    public int getPort(){
        return Utils.getInt(props, "port");
    }

    public Properties getProperties() {
        return props;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public int getReconnectTimeInterval() {
        return reconnectTimeInterval;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }
}
