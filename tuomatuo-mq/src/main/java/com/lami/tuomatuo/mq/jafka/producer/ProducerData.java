package com.lami.tuomatuo.mq.jafka.producer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data to be sent using the Producer send API
 *
 * Created by xjk on 2016/10/19.
 */
public class ProducerData<K, V> {

    /** the topic under which the message is to be published */
    private String topic;

    /** the key used by the partitioner to pick a broker partition */
    private K key;

    /** variable length data to be published as Jafka messages under topic */
    private List<V> data;

    public ProducerData(String topic, K key, List<V> data) {
        super();
        this.topic = topic;
        this.key = key;
        this.data = data;
    }

    public ProducerData(String topic, List<V> data) {
        this.topic = topic;
        this.data = data;
    }

    public ProducerData(String topic, V data) {
        this.topic = topic;
        getData().add(data);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public List<V> getData(){
        if(data == null){
            data = new ArrayList<V>();
        }
        return data;
    }

    public void setData(List<V> data){
        this.data =  data;
    }
}
