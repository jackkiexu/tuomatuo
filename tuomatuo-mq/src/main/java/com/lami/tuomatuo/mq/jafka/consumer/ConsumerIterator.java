package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.common.ConsumerTimeoutException;
import com.lami.tuomatuo.mq.jafka.message.MessageAndOffset;
import com.lami.tuomatuo.mq.jafka.mx.ConsumerTopicStat;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;
import com.lami.tuomatuo.mq.jafka.utils.IteratorTemplate;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by xjk on 2016/10/31.
 */
public class ConsumerIterator<T> extends IteratorTemplate<T> {

    private static Logger logger = Logger.getLogger(ConsumerIterator.class);

    public String topic;

    public BlockingQueue<FetchedDataChunk> queue;

    public int consumerTimeoutMs;

    public Decoder<T> decoder;

    private AtomicReference<Iterator<MessageAndOffset>> current = new AtomicReference<Iterator<MessageAndOffset>>();

    private PartitionTopicInfo currentTopicInfo = null;

    private long consumerOffset = -1l;

    public ConsumerIterator(String topic, BlockingQueue<FetchedDataChunk> queue, int consumerTimeoutMs, Decoder<T> decoder) {
        super();
        this.topic = topic;
        this.queue = queue;
        this.consumerTimeoutMs = consumerTimeoutMs;
        this.decoder = decoder;
    }

    @Override
    public T next() {
        T decodeMessage = super.next();
        if(consumerOffset < 0){
            throw new IllegalStateException("Offset returned by the message set is invalid " + consumerOffset);
        }
        currentTopicInfo.resetConsumerOffset(consumerOffset);
        ConsumerTopicStat.getComsumerTopicStat(topic).recordMessagePerTopic(1);
        return decodeMessage;
    }

    @Override
    protected T makeNext() {
        try {
            return makeNext0();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected T makeNext0() throws InterruptedException{
        FetchedDataChunk currentDataChunk = null;
        Iterator<MessageAndOffset> localCurrent = null;
        if(localCurrent == null || !localCurrent.hasNext()){
            if(consumerTimeoutMs < 0){
                currentDataChunk = queue.take();
            }else{
                currentDataChunk = queue.poll(consumerTimeoutMs, TimeUnit.MILLISECONDS);
                if(currentDataChunk == null){
                    resetState();
                    throw new ConsumerTimeoutException("consumer timeout in " + consumerTimeoutMs + "ms");
                }
            }

            if(currentDataChunk == ZookeeperConsumerConnector.SHUTDOWN_COMMAND){
                queue.offer(currentDataChunk);
                return allDone();
            }else{
                currentTopicInfo = currentDataChunk.topicInfo;
                if(currentTopicInfo.getConsumedOffset() != currentDataChunk.fetchOffset){
                    logger.info(String.format("command offset: %d doesn't match fetch offset: %d for %s; \n Consumer may lose data", currentTopicInfo.getConsumedOffset(), currentDataChunk.fetchOffset, currentTopicInfo));
                    currentTopicInfo.resetConsumerOffset(currentDataChunk.fetchOffset);
                }
                localCurrent = currentDataChunk.messages.iterator();
                current.set(localCurrent);
            }
        }
        MessageAndOffset item = localCurrent.next();
        consumerOffset = item.offset;
        return decoder.toEvent(item.message);
    }

    public void clearCurrentChunk(){
        logger.info("Clearing the current data chunk for this consumer iterator");
        current.set(null);
    }
}
