package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;

import java.io.Closeable;
import java.util.Map;
import java.util.SortedSet;

/**
 * Created by xjk on 2016/10/19.
 */
public interface BrokerPartitionInfo extends Closeable {

    /**
     * Return a sequence of (brokerId, numPartitions)
     * @param topic
     * @return
     */
    SortedSet<Partition> getBrokerPartitionInfo(String topic);

    /**
     * Generate the host and port information for the broker identified by the given broker id
     * @param brokerId
     * @return
     */
    Broker getBrokerInfo(int brokerId);

    /**
     * Generate a mapping from broker id to the host and port for all brokers
     * @return
     */
    Map<Integer, Broker> getAllBrokerInfo();

    /**
     * This is relevant to the ZKBrokerPartitionInfo. It updates the ZK
     * cache by reading from zookeeper and recreating the data structures.
     * This API is invoked by the producer, when it detects that the ZK
     * cache of ZKBrokerPartitionInfo is state
     */
    void updateInfo();

    /**
     * Clean up
     */
    void close();

    public interface Callback{
        void producerCbk(int bid, String host, int port);
    }

}
