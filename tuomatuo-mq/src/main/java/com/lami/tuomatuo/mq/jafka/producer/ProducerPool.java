package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.InvalidConfigException;
import com.lami.tuomatuo.mq.jafka.common.UnavailableProducerException;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.producer.async.*;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xjk on 2016/10/19.
 */
public class ProducerPool<V> implements Closeable {

    private static final Logger logger = Logger.getLogger(ProducerPool.class);

    private ProducerConfig config;

    private Encoder<V> serializer;

    private ConcurrentMap<Integer, SyncProducer> syncProducers;

    private ConcurrentMap<Integer, AsyncProducer<V>> asyncProducers;

    private EventHandler<V> eventHandler;

    private CallbackHandler<V> callbackHandler;

    private boolean sync = true;

    public ProducerPool(ProducerConfig config,
                        Encoder<V> serializer,
                        ConcurrentMap<Integer, SyncProducer> syncProducers,
                        ConcurrentMap<Integer, AsyncProducer<V>> asyncProducers,
                        EventHandler<V> inputEventHandler,
                        CallbackHandler<V> callbackHandler) {
        super();
        this.config = config;
        this.serializer = serializer;
        this.syncProducers = syncProducers;
        this.asyncProducers = asyncProducers;
        this.eventHandler = inputEventHandler != null ? inputEventHandler : new DefaultEventHandler<V>(config, callbackHandler);
        this.callbackHandler = callbackHandler;
        if(serializer == null){
            throw new InvalidConfigException("serializer passed in is null");
        }
        this.sync = !"async".equalsIgnoreCase(config.getProducerType());
    }

    public ProducerPool(ProducerConfig config, Encoder<V> serializer, EventHandler<V> eventHandler, CallbackHandler<V> callbackHandler) {
        this(config,
                serializer,
                new ConcurrentHashMap<Integer, SyncProducer>(),
                new ConcurrentHashMap<Integer, AsyncProducer<V>>(),
                eventHandler,
                callbackHandler);
    }

    public ProducerPool(ProducerConfig config, Encoder<V> serializer) {
        this(config,
                serializer,
                new ConcurrentHashMap<Integer, SyncProducer>(),
                new ConcurrentHashMap<Integer, AsyncProducer<V>>(),
                (EventHandler<V>) Utils.getObject(config.getEventHandler()),
                (CallbackHandler<V>)Utils.getObject(config.getCbkHandler()));
    }

    /**
     * add a new producer, either synchronous or asynchronous, connecting
     * to the specified broker
     * @param bid the id of the broker
     * @param host the hostname of the broker
     * @param port the port of the broker
     */
    public void addProducer(Broker broker){
        Properties props = new Properties();
        props.put("host", broker.host);
        props.put("port", "" + broker.port);
        props.putAll(config.getProperties());
        if(sync){
            SyncProducer producer = new SyncProducer(new SyncProducerConfig(props));
            logger.info("Creating sync producer for broker id = " + broker.id + " at " + broker.host + " : " + broker.port);
            syncProducers.put(broker.id, producer);
        }else{
            AsyncProducer<V> producer = new AsyncProducer<V>(new AsyncProducerConfig(props),
                    new SyncProducer(new SyncProducerConfig(props)),
                    serializer,
                    eventHandler,
                    config.getEventHandlerProperties(),
                    this.callbackHandler,
                    config.getCbkHandlerProperties());
            producer.start();
            logger.info("Creating async producer for broker Id = " + broker.id + " at " + broker.host + " : " + broker.port);
            asyncProducers.put(broker.id, producer);
        }
    }

    /**
     * selects either a synchronous or an asynchronous producer, for
     * the specified broker id and calls the send API on the selected
     * producer to publish the data to the specified broker partition
     *
     * @param poolData the producer pool request object
     */
    public void send(ProducerPoolData<V> poolData){
        send(Arrays.asList(poolData));
    }

    public void send(List<ProducerPoolData<V>> poolData){
        if(sync){
            syncSend(poolData);
        }else{
            asyncSend(poolData);
        }
    }

    private void asyncSend(List<ProducerPoolData<V>> poolData){
        for(ProducerPoolData<V> ppd : poolData){
            AsyncProducer<V> asyncProducer = asyncProducers.get(ppd.partition.brokerId);
            for(V v : ppd.data){
                asyncProducer.send(ppd.topic, v, ppd.partition.partId);
            }
        }
    }

    private void syncSend(List<ProducerPoolData<V>> poolData){
        Map<Integer, List<ProducerRequest>> topicBrokerIdData = new HashMap<Integer, List<ProducerRequest>>();
        for(ProducerPoolData<V> ppd : poolData){
            List<ProducerRequest> messageSets = topicBrokerIdData.get(ppd.partition.brokerId);
            if(messageSets == null){
                messageSets = new ArrayList<ProducerRequest>();
                topicBrokerIdData.put(ppd.partition.brokerId, messageSets);
            }
            Message[] messages = new Message[ppd.data.size()];
            int index = 0;
            for(V v : ppd.data){
                messages[index] = serializer.toMessage(v);
                index++;
            }
            ByteBufferMessageSet bbms = new ByteBufferMessageSet(config.getCompressionCodec(), messages);
            messageSets.add(new ProducerRequest(ppd.topic, ppd.partition.partId, bbms));
        }

        for(Map.Entry<Integer, List<ProducerRequest>> e : topicBrokerIdData.entrySet()){
            SyncProducer producer = syncProducers.get(e.getKey());
            if(producer == null){
                throw new UnavailableProducerException("Producer pool has been initialized correctly. " + "Sync Producer for broker " + e.getKey() + " does not exist in the pool");
            }
            if(e.getValue().size() == 1){
                ProducerRequest request = e.getValue().get(0);
                producer.send(request.getTopic(), request.getPartition(), request.getMessages());
            }else{
                producer.multiSend(e.getValue());
            }
        }
    }

    /**
     * Close all producers in the pool
     * @throws IOException
     */
    public void close() throws IOException {
        logger.info("Closing all sync producers");
        if(sync){
            for(SyncProducer p : syncProducers.values()){
                p.close();
            }
        }else{
            for(AsyncProducer<V> p : asyncProducers.values()){
                p.close();
            }
        }
    }

    /**
     * This constructs and returns the request object for the producer pool
     * @param topic the topic to which the data should be published
     * @param bidPid the broker id and partition id
     * @param data the data to be published
     */
    public ProducerPoolData<V> getProducerPoolData(String topic, Partition bidPid, List<V> data){
        return new ProducerPoolData<V>(topic, bidPid, data);
    }
}
