package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by xjk on 2016/10/31.
 */

@ClientSide
public class MessageStream<T> implements Iterable<T> {

    public String topic;

    public BlockingQueue<FetchedDataChunk> queue;

    public int consumerTimeoutMs;

    public Decoder<T> decoder;

    private ConsumerIterator<T> consumerIterator;

    public MessageStream(String topic, BlockingQueue<FetchedDataChunk> queue, int consumerTimeoutMs, Decoder<T> decoder) {
        super();
        this.topic = topic;
        this.queue = queue;
        this.consumerTimeoutMs = consumerTimeoutMs;
        this.decoder = decoder;
        this.consumerIterator = new ConsumerIterator<T>(topic, queue, consumerTimeoutMs, decoder);
    }

    public Iterator<T> iterator() {
        return consumerIterator;
    }

    /**
     * This method clears the queue being iterated duringthe consumer
     * rebalancing. This is mainly to reduce the number of duplicates
     * received by the consumer
     */
    public void clear(){
        consumerIterator.clearCurrentChunk();
    }

}
