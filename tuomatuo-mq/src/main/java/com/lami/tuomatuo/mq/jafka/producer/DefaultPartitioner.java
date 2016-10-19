package com.lami.tuomatuo.mq.jafka.producer;

import java.util.Random;

/**
 * Created by xjk on 2016/10/19.
 */
public class DefaultPartitioner<T> implements Partitioner<T> {

    private final Random random = new Random();

    public int partition(T key, int numPartitions) {
        if(key == null){
            return random.nextInt(numPartitions);
        }
        return Math.abs(key.hashCode()) % numPartitions;
    }
}
