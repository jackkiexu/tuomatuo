package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.producer.async.AsyncProducerConfigShared;
import com.lami.tuomatuo.mq.jafka.utils.ZkConfig;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/19.
 */
@ClientSide
public class ProducerConfig extends ZkConfig implements SyncProducerConfigShared, AsyncProducerConfigShared {

    SyncProducerConfigShared syncConfigShared;

    AsyncProducerConfigShared asyncProducerConfigShared;

    public ProducerConfig(Properties props) {
        super(props);
        syncConfigShared = new Sy
    }

    public Properties getProperties() {
        return null;
    }

    public int getQueueTime() {
        return 0;
    }

    public int getQueueSize() {
        return 0;
    }

    public int getEnqueueTimeoutMs() {
        return 0;
    }

    public int getBatchSize() {
        return 0;
    }

    public String getCbkHandler() {
        return null;
    }

    public Properties getCbkHandlerProperties() {
        return null;
    }

    public String getEventHandler() {
        return null;
    }

    public Properties getEventHandlerProperties() {
        return null;
    }
}
