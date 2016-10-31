package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Main interface for consumer
 *
 * Created by xjk on 2016/10/31.
 */
public interface ConsumerConnector extends Closeable{

    /**
     * Create a list of {@list MessageStream} for each topic
     * @param topicCountMap a map of (topic, #stream) pair
     * @param decoder message decoder
     * @return a map of (topic, list, of MessageStream) pair. The number of item
     *         in the list is # stream. Each MessageStream supports
     *         an interator of messages
     */
    <T> Map<String, List<MessageStream<T>>> createMessageStreams(Map<String, Integer> topicCountMap, Decoder<T> decoder);


    /**
     * Commit the offsets of all broker partitions connected by this connector
     */
    void commitOffsets();

    /**
     * Shut down the connector
     */
    public void close() throws IOException;
}
