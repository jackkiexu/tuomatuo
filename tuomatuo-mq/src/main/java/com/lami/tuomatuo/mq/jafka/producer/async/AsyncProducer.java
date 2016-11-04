package com.lami.tuomatuo.mq.jafka.producer.async;

import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.common.AsyncProducerInterruptedException;
import com.lami.tuomatuo.mq.jafka.common.QueueClosedException;
import com.lami.tuomatuo.mq.jafka.common.QueueFullException;
import com.lami.tuomatuo.mq.jafka.mx.AsyncProducerQueueSizeStats;
import com.lami.tuomatuo.mq.jafka.mx.AsyncProducerStats;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import com.lami.tuomatuo.mq.jafka.producer.SyncProducer;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xjk on 2016/10/19.
 */
public class AsyncProducer<T> implements Closeable {

    private static final Logger logger = Logger.getLogger(AsyncProducer.class);

    private static final Random random = new Random();

    private static final String ProducerQueueSizeMBeanName = "jafka.producer.Producer:type=AsyncProducerQueueSizeStats";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public AsyncProducerConfig config;

    public SyncProducer producer;

    public Encoder<T> serializer;

    public EventHandler<T> eventHandler;

    public Properties eventHandlerProperties;

    public CallbackHandler<T> callbackHandler;

    public Properties callbackhandlerProperties;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public AtomicBoolean closed = new AtomicBoolean(false);

    public LinkedBlockingQueue<QueueItem<T>> queue;

    public int asyncProducerID = AsyncProducer.random.nextInt();

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public ProducerSendThread<T> sendThread;

    private int enqueueTimeoutMs;

    public AsyncProducer(AsyncProducerConfig config,
                         SyncProducer producer,
                         Encoder<T> serializer,
                         EventHandler<T> eventHandler,
                         Properties eventHandlerProperties,
                         CallbackHandler<T> callbackHandler,
                         Properties callbackhandlerProperties) {
        super();
        this.config = config;
        this.producer = producer;
        this.serializer = serializer;
        this.eventHandler = eventHandler;
        this.eventHandlerProperties = eventHandlerProperties;
        this.callbackHandler = callbackHandler;
        this.callbackhandlerProperties = callbackhandlerProperties;

        this.queue = new LinkedBlockingQueue<QueueItem<T>>(config.getQueueSize());

        if(eventHandler != null){
            eventHandler.init(eventHandlerProperties);
        }

        if(callbackHandler != null){
            callbackHandler.init(callbackhandlerProperties);
        }

        this.sendThread = new ProducerSendThread<T>("ProducerSendThread-" + asyncProducerID,
                queue,
                serializer,
                producer,
                eventHandler != null ? eventHandler : new DefaultEventHandler<T>(new ProducerConfig(config.getProperties()), callbackHandler),
                callbackHandler,
                config.getQueueTime(),
                config.getBatchSize()
                );
        this.sendThread.setDaemon(false);
        //TODO
        AsyncProducerQueueSizeStats<T> stats = new AsyncProducerQueueSizeStats<T>(queue);
        stats.setMbeanName(ProducerQueueSizeMBeanName + "-" + asyncProducerID);
        Utils.registerMBean(stats);
    }

    public AsyncProducer(AsyncProducerConfig config) {
        this(config,
                new SyncProducer(config),
                (Encoder<T>)Utils.getObject(config.getSerializerClass()),
                (EventHandler<T>)Utils.getObject(config.getEventHandler()),
                config.getEventHandlerProperties(),
                (CallbackHandler<T>)Utils.getObject(config.getCbkHandler()),
                config.getCbkHandlerProperties());
    }

    public void start(){
        sendThread.start();
    }

    public void send(String topic, T event){
        send(topic, event, ProducerRequest.RandomPartition);
    }

    public void send(String topic, T event, int partition){
        AsyncProducerStats.recordEvent();
        if(closed.get()){
            throw new QueueClosedException("Attempt to add event to a closed queue");
        }
        QueueItem<T> data = new QueueItem<T>(event, partition, topic);
        if(this.callbackHandler != null){
            data = this.callbackHandler.beforeEnqueue(data);
        }
        boolean added = false;

        try {
            if(enqueueTimeoutMs == 0){
                added = queue.offer(data);
            }else if(enqueueTimeoutMs < 0){
                queue.put(data);
                added = true;
            }else {
                added = queue.offer(data, enqueueTimeoutMs, TimeUnit.MICROSECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new AsyncProducerInterruptedException(e);
        }

        if(this.callbackHandler != null){
            this.callbackHandler.afterEnqueue(data, added);
        }
        if(!added){
            AsyncProducerStats.recordDroppedEvents();
            throw new QueueFullException("Event queue is full of unset messages, could not send event : " + event);
        }
    }

    public void close() throws IOException {
        if(this.callbackHandler != null){
            callbackHandler.close();
        }
        closed.set(true);
        sendThread.shutdown();
        sendThread.awaitShutdown();
        producer.close();
        logger.info("Closed AsyncProducer");
    }
}
