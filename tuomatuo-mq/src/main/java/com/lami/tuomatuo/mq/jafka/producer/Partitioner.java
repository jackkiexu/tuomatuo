package com.lami.tuomatuo.mq.jafka.producer;

/**
 * Created by xjk on 2016/10/19.
 */
public interface Partitioner<T> {

    /**
     * Uses the key to calculate a partition bucket id for routing the data
     * to the appropriate broker partition
     *
     * @param key
     * @param numPartitions
     * @return
     */
    int partition(T key, int numPartitions);
}
