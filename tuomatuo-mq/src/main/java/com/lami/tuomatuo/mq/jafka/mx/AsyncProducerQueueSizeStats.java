package com.lami.tuomatuo.mq.jafka.mx;

import com.sohu.jafka.producer.async.QueueItem;

import java.util.concurrent.BlockingQueue;

/**
 * Created by xjk on 2016/10/11.
 */
public class AsyncProducerQueueSizeStats<T> implements AsyncProducerQueueSizeStatsMBean, IMBeanName {

    BlockingQueue<QueueItem<T>> queue;

    private String mbeanName;

    public AsyncProducerQueueSizeStats(BlockingQueue<QueueItem<T>> queue) {
        super();
        this.queue = queue;
    }

    public int getAsyncProducerQueueSize() {
        return queue.size();
    }

    public String getMbeanName() {
        return mbeanName;
    }

    public void setMbeanName(String mbeanName) {
        this.mbeanName = mbeanName;
    }
}
