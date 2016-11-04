package com.lami.tuomatuo.mq.jafka.api;

/**
 * Created by xjk on 2016/11/4.
 */
public interface PartitionChooser {

    int choosePartition(String topic);

}
