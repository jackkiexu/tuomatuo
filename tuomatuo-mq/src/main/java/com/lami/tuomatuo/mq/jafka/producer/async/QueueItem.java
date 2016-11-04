package com.lami.tuomatuo.mq.jafka.producer.async;

/**
 * Created by xjk on 2016/10/19.
 */
public class QueueItem<T> {

    public T data;

    public int partition;

    public String topic;

    public QueueItem(T data, int partition, String topic) {
        super();
        this.data = data;
        this.partition = partition;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "QueueItem{" +
                "data=" + data +
                ", partition=" + partition +
                ", topic='" + topic + '\'' +
                '}';
    }
}
