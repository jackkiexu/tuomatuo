package com.lami.tuomatuo.mq.jafka.consumer;


/**
 * Created by xjk on 10/31/16.
 */
public class Consumer {

    /**
     * create a ConsumerConnector
     *
     * @param config at the minium, need to specify these properties:
     * <pre>
     *  groupid: the consumer
     *  zk.connect: the zookeeper connection string
     * </pre>
     * @return
     */
    public static ConsumerConnector create(ConsumerConfig config) {
//        ConsumerConnector consumerConnector = new ZookeeperConsumerConnector(config);
        //register mbean
        //Utils.registerMBean(consumerConnector, "jafka:type=jafka.ConsumerStats");
        return null;
    }

}