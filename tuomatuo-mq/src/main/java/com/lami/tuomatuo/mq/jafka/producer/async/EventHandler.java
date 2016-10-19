package com.lami.tuomatuo.mq.jafka.producer.async;

import com.lami.tuomatuo.mq.jafka.producer.SyncProducer;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;

import java.io.Closeable;
import java.util.List;
import java.util.Properties;

/**
 * Handler that dispathes the batched data from the queue of the
 * asynchronous producer
 *
 * Created by xjk on 2016/10/19.
 */
public interface EventHandler<T> extends Closeable {

    /**
     * Initializes the event handler using a Properties Object
     * @param properties
     */
    void init(Properties properties);

    /**
     * Callback to dispatch the batched data and send it to a Jafka server
     * @param events
     * @param producer
     * @param encoder
     */
    void handle(List<QueueItem<T>> events, SyncProducer producer, Encoder<T> encoder);

    /**
     * Cleans up and shuts down the event handler
     */
    void close();
}
