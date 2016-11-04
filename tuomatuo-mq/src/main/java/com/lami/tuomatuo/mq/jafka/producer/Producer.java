package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.InvalidPartitionException;
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

    private final Logger logger = Logger.getLogger(Producer.class);

    public ProducerConfig config;

    public Partitioner<K> partitioner;

    public ProducerPool<V> producerPool;

    public boolean populateProducerPool;

    public BrokerPartitionInfo brokerPartitionInfo;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private final AtomicBoolean hasShutdown = new AtomicBoolean(false);

    private final Random random = new Random();

    private boolean zkEnabled;

    public Producer(ProducerConfig config){

    }

    private void zkSend(ProducerData<K, V> data){
        int numRetries = 0;

    }

    private int getPartition(K key, int numPartitions){
        if(numPartitions <= 0){
            throw new InvalidPartitionException("Invalid number of parittion: " + numPartitions + " Valid values are > 0");
        }
        int partition = key == null ? random.nextInt(numPartitions) : partitioner.partition(key, numPartitions);
        if(partition < 0 || partition >= numPartitions){
            throw new InvalidPartitionException("Invalid partition id : " + partition + " Valid values are in the range inclusive [0, " + (numPartitions - 1) + " ]");
        }
        return partition;
    }

    public void producerCbk(int bid, String host, int port){
        if(populateProducerPool){
            producerPool.addProducer(new Broker(bid, host, host, port));
        }else{
            logger.info("Skipping the callback since populateProducerPool = false");
        }
    }

    private ProducerPoolData<V> create(ProducerData<K, V> pd){
        Collection<Partition> topicPartitionsList = getPartitionListForTopic(pd);
        int randomBrokerId = random.nextInt(topicPartitionsList.size());
        Partition brokerIdPartition = new ArrayList<Partition>(topicPartitionsList).get(randomBrokerId);
        return this.producerPool.getProducerPoolData(pd.getTopic(),
                new Partition(brokerIdPartition.brokerId, ProducerRequest.RandomPartition), pd.getData());
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
