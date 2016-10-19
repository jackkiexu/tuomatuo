package com.lami.tuomatuo.mq.jafka.producer;

import java.util.List;

/**
 * Created by xjk on 2016/10/19.
 */
public class ProducerData<K, V> {

    /** the topic under which the message is to be published */
    private String topic;

    /** the key used by the partitioner to pick a broker partition */
    private K key;

    /** variable length data to be published as Jafka messages under topic */
    private List<V> data;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
