package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.InvalidPartitionException;
import com.lami.tuomatuo.mq.jafka.common.NoBrokersForPartitionException;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.producer.async.CallbackHandler;
import com.lami.tuomatuo.mq.jafka.producer.async.EventHandler;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import com.lami.tuomatuo.mq.jafka.utils.ZkConfig;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
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

    public Producer(ProducerConfig config,
                    Partitioner<K> partitioner,
                    ProducerPool<V> producerPool,
                    boolean populateProducerPool,
                    BrokerPartitionInfo brokerPartitionInfo ){
        super();
        this.config = config;
        this.partitioner = partitioner;
        this.producerPool = producerPool;
        this.populateProducerPool = populateProducerPool;
        this.brokerPartitionInfo = brokerPartitionInfo;

        //
        this.zkEnabled = config.getZkConnect() != null;
        if(this.brokerPartitionInfo == null){
            if(this.zkEnabled){
                Properties zkProps = new Properties();
                zkProps.put("zk.connect", config.getZkConnect());
                zkProps.put("zk.sessiontimeout.ms", config.getZkSessionTimeoutMs());
                zkProps.put("zk.connectiontimeout.ms", "" + config.getZkConnectionTimeoutMs());
                zkProps.put("zk.synctime.ms", "" + config.getZkSyncTimeMs());
                this.brokerPartitionInfo = new ZKBrokerPartitionInfo(new ZkConfig(zkProps), this);
            }else{
                this.brokerPartitionInfo = new ConfigBrokerPartitionInfo(config);
            }
        }

        // pool of producers one per broker
        if(this.populateProducerPool){
            for(Map.Entry<Integer, Broker> e : this.brokerPartitionInfo.getAllBrokerInfo().entrySet()){
                Broker b = e.getValue();
                producerPool.addProducer(new Broker(e.getKey(), b.host, b.host, b.port));
            }
        }
    }

    /**
     * This constructor can be used when all config parameters will be
     * specified through the ProducerConfig object
     *
     * @param config Producer Configuration object
     */
    public Producer(ProducerConfig config) {
        this(config,
                (Partitioner<K>) Utils.getObject(config.getPartitionerClass()),
                new ProducerPool<V>(config, (Encoder<V>)Utils.getObject(config.getSerializerClass())),
                true,
                null);
    }

    /**
     * This constractor can be used to provide pre-instantiated objects for
     * all config parameters that would otherwise be instantiated via
     * reflection. i.e encoder, partitioner, event handler and callback
     * handler. If you use this constractor, encoder, eventHandler,
     * callback handler and partitioner will not be picked up from the
     * config
     *
     *
     * @param config Producer Configuration object
     *
     * @param encoder Encoder used to convert an object of type V to a
     *                jafka.message.Message. If this is null it throws an
     *                InvalidConfigException
     *
     * @param eventHandler the class that implements
     *                     jafka.producer.async.IEventHandler[T] used to dispatch a
     *                     batch of produce requests, using an instance of
     *                     jafka.producer.SyncProducer. If this is null, it uses the
     *                     DefaultEventHandler
     *
     * @param cbkHandler the class that implements
     *                   jafka.producer.async.CannbackHandler[T] used to inject
     *                   callback at various stages of the
     *                   jafka.producer.AsyncProducer pipeline. If this is null, the
     *                   producer does not use the callback handler and hence does not
     *                   onvoke any callbacks
     *
     * @param partitioner class that implements the
     *                    jafka.producer.Partitioner[K], used to supply a custom
     *                    partitioning strategy on the message key (of type K) that is
     *                    specified through the ProducerData[K, T] object in the send
     *                    API. If this null, producer uses DefaultPartitioner
     */
    public Producer(ProducerConfig config, Encoder<V> encoder, EventHandler<V> eventHandler, CallbackHandler<V> cbkHandler, Partitioner<K> partitioner){
        this(config,
                partitioner == null ? new DefaultPartitioner<K>() : partitioner,
                new ProducerPool<V>(config, encoder, eventHandler, cbkHandler),
                true,
                null);
    }

    public void send(ProducerData<K, V> data){
        if(data == null) return;
        if(zkEnabled){
            zkSend(data);
        }else{
            configSend(data);
        }
    }

    private void configSend(ProducerData<K, V> data){
        producerPool.send(create(data));
    }

    private void zkSend(ProducerData<K, V> data){
        int numRetries = 0;
        Broker brokerOnfoOpt = null;
        Partition brokerIdPartition = null;
        while(numRetries <= config.getZkReadRetries() && brokerOnfoOpt == null){
            if(numRetries > 0){
                logger.info("Try # " + numRetries + " ZK producer cache is stale. Refreshing it by reading from ZK again");
                brokerPartitionInfo.updateInfo();
            }
            List<Partition> partitions = new ArrayList<Partition>(getPartitionListForTopic(data));
            brokerIdPartition = partitions.get(getPartition(data.getKey(), partitions.size()));
            if(brokerIdPartition != null){
                brokerOnfoOpt = brokerPartitionInfo.getBrokerInfo(brokerIdPartition.brokerId);
            }
            numRetries++;

        }
        if(brokerOnfoOpt == null){
            throw new NoBrokersForPartitionException("Invalid Zookeeper state. Failed to get partition for topic: " + data.getTopic() + " and key : " + data.getKey());
        }

        ProducerPoolData<V> ppd = producerPool.getProducerPoolData(data.getTopic(),
                new Partition(brokerIdPartition.brokerId, brokerIdPartition.partId),
                data.getData());
        producerPool.send(ppd);

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
