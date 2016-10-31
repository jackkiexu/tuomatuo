package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xjk on 2016/10/31.
 */
public class PartitionTopicInfo {

    private static final Logger logger = Logger.getLogger(PartitionTopicInfo.class);

    public String topic;

    public int brokerId;

    private BlockingQueue<FetchedDataChunk> chunkQueue;

    private AtomicLong consumerOffset;

    private AtomicLong fetchOffset;

    public Partition partition;

    public PartitionTopicInfo(String topic, int brokerId, BlockingQueue<FetchedDataChunk> chunkQueue, AtomicLong consumerOffset, AtomicLong fetchOffset, Partition partition) {
        super();
        this.topic = topic;
        this.brokerId = brokerId;
        this.chunkQueue = chunkQueue;
        this.consumerOffset = consumerOffset;
        this.fetchOffset = fetchOffset;
        this.partition = partition;
    }


    /**
     * return the comsumerOffset
     */
    public long getConsumedOffset(){
        return consumerOffset.get();
    }

    public long getFetchOffset(){
        return fetchOffset.get();
    }

    public void resetConsumerOffset(long newConsumerOffset){
        consumerOffset.set(newConsumerOffset);
    }

    public void resetFetchOffset(long newFetchOffset){
        fetchOffset.set(newFetchOffset);
    }

    public long enqueue(ByteBufferMessageSet messages, long fetchOffset) throws InterruptedException{
        long size = messages.getValidBytes();
        if(size > 0){
            long oldOffset = this.fetchOffset.get();
            chunkQueue.put(new FetchedDataChunk(messages, this, fetchOffset));
            long newOffsetset = this.fetchOffset.addAndGet(size);
            if(logger.isDebugEnabled()){
                logger.debug("update fetchset (origin+size=newOffset) => " + oldOffset + " + " + size + "= " + newOffsetset);
            }
        }
        return size;
    }

    public void enqueueError(Exception e, long fetchOffset) throws InterruptedException{
        ByteBufferMessageSet messageSet = new ByteBufferMessageSet(ErrorMapping.EMPTY_BUFFER, 0, ErrorMapping.valueOf(e));
        chunkQueue.put(new FetchedDataChunk(messageSet, this, fetchOffset));
    }


    @Override
    public String toString() {
        return "PartitionTopicInfo{" +
                "topic='" + topic + '\'' +
                ", brokerId=" + brokerId +
                ", consumerOffset=" + consumerOffset +
                ", fetchOffset=" + fetchOffset +
                ", partition=" + partition +
                '}';
    }
}
