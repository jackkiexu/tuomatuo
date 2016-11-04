package com.lami.tuomatuo.mq.jafka.producer.async;

import com.lami.tuomatuo.mq.jafka.producer.SyncProducerConfig;
import com.lami.tuomatuo.mq.jafka.producer.serializer.DefaultEncoder;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.util.Properties;

/**
 * Created by xjk on 2016/11/4.
 */
public class AsyncProducerConfig extends SyncProducerConfig implements AsyncProducerConfigShared{

    public AsyncProducerConfig(Properties props) {
        super(props);
    }

    public int getQueueTime() {
        return Utils.getInt(props, "queue.time", 5000);
    }

    public int getQueueSize() {
        return Utils.getInt(props, "queue.size", 10000);
    }

    public int getEnqueueTimeoutMs() {
        return Utils.getInt(props, "queue.enqueueTimeout.ms", 0);
    }

    public int getBatchSize() {
        return Utils.getInt(props, "batch.size", 200);
    }

    public String getSerializerClass(){
        return Utils.getString(props, "serializer.class", DefaultEncoder.class.getName());
    }

    public String getCbkHandler() {
        return Utils.getString(props, "callback.handler", null);
    }

    public Properties getCbkHandlerProperties() {
        return Utils.getProps(props, "callback.handler.props", null);
    }

    public Properties getEventHandlerProperties(){
        return Utils.getProps(props, "event.handler.props", null);
    }

    public String getEventHandler() {
        return Utils.getString(props, "event.handler", null);
    }


}
