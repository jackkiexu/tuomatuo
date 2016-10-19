package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.cluster.Partition;

import java.util.List;

/**
 * Created by xjk on 2016/10/19.
 */
public class ProducerPoolData<V> {

    public String topic;

    public Partition partition;

    public List<V> data;

    public ProducerPoolData(String topic, Partition partition, List<V> data) {
        super();
        this.topic = topic;
        this.partition = partition;
        this.data = data;
    }
}
