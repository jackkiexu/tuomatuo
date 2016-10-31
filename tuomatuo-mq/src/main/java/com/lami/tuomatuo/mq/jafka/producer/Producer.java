package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.NoBrokersForPartitionException;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Message producer
 *
 * Created by xjk on 2016/10/19.
 */
@ClientSide
public class Producer<K, V> implements BrokerPartitionInfo.Callback, Closeable {

    ProducerConfig config;

    Partitioner<K> partitioner;

    ProducerPool<V> producerPool;

    boolean populateProducerPool;

    BrokerPartitionInfo brokerPartitionInfo;

    private final Logger logger = Logger.getLogger(Producer.class);

    private final AtomicBoolean hasShutdown = new AtomicBoolean(false);

    private final Random random = new Random();

    private boolean zkEnabled;



    public void producerCbk(int bid, String host, int port) {

    }

    public Producer(ProducerConfig config){

    }

    private ProducerPoolData<V> create(ProducerData<K, V> pd){
        Collection<Partition> topicPartitionsList = getPartitionListForTopic(pd);
        int randomBrokerId = random.nextInt(topicPartitionsList.size());
        Partition brokerIdPartition = new ArrayList<Partition>(topicPartitionsList).get(randomBrokerId);
//        return this.producerPool.
        return null;
    }

    private Collection<Partition> getPartitionListForTopic(ProducerData<K, V> pd){
        SortedSet<Partition> topicPartitionsList = brokerPartitionInfo.getBrokerPartitionInfo(pd.getTopic());
        if(topicPartitionsList.size() == 0){
            throw new NoBrokersForPartitionException("Partition=" +pd.getTopic());
        }
        return topicPartitionsList;
    }

    public void close() throws IOException {
        if(hasShutdown.compareAndSet(false, true)){
            Closer.closeQuietly(producerPool);
            Closer.closeQuietly(brokerPartitionInfo);
        }
    }


}
