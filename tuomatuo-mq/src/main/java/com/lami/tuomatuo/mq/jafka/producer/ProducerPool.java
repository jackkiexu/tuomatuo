package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.producer.async.AsyncProducer;
import com.lami.tuomatuo.mq.jafka.producer.async.EventHandler;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xjk on 2016/10/19.
 */
public class ProducerPool<V> implements Closeable {

    private ProducerConfig config;

    private Encoder<V> serializer;

    private ConcurrentMap<Integer, SyncProducer> syncProducers;

    private ConcurrentMap<Integer, AsyncProducer<V>> asyncProducers;

    private EventHandler<V> eventHandler;



    public void close() throws IOException {

    }
}
