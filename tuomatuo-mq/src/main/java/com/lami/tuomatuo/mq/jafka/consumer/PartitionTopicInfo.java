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

    public final String topic;

    public final int brokerId;

    private final BlockingQueue<FetchedDataChunk> chunkQueue;

    private final AtomicLong consumedOffset;

    private final AtomicLong fetchedOffset;

    final Partition partition;

    public PartitionTopicInfo(String topic, //
                              int brokerId, //
                              Partition partition,//
                              BlockingQueue<FetchedDataChunk> chunkQueue, //
                              AtomicLong consumedOffset, //
                              AtomicLong fetchedOffset) {
        super();
        this.topic = topic;
        this.partition = partition;
        this.brokerId = brokerId;
        this.chunkQueue = chunkQueue;
        this.consumedOffset = consumedOffset;
        this.fetchedOffset = fetchedOffset;
    }

    /**
     * @return the consumedOffset
     */
    public long getConsumedOffset() {
        return consumedOffset.get();
    }

    /**
     * @return the fetchedOffset
     */
    public long getFetchedOffset() {
        return fetchedOffset.get();
    }

    public void resetConsumeOffset(long newConsumeOffset) {
        consumedOffset.set(newConsumeOffset);
    }

    public void resetFetchOffset(long newFetchOffset) {
        fetchedOffset.set(newFetchOffset);
    }

    public long enqueue(ByteBufferMessageSet messages, long fetchOffset) throws InterruptedException {
        long size = messages.getValidBytes();
        if (size > 0) {
            final long oldOffset = fetchedOffset.get();
            chunkQueue.put(new FetchedDataChunk(messages, this, fetchOffset));
            long newOffset = fetchedOffset.addAndGet(size);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("updated fetchset (origin+size=newOffset) => %d + %d = %d", oldOffset, size, newOffset));
            }
        }
        return size;
    }

    @Override
    public String toString() {
        return topic + ":" + partition + ": fetched offset = " + fetchedOffset.get() + ": consumed offset = " + consumedOffset.get();
    }

    /**
     * @param e
     * @throws InterruptedException
     */
    public void enqueueError(Exception e, long fetchOffset) throws InterruptedException {
        ByteBufferMessageSet messages = new ByteBufferMessageSet(ErrorMapping.EMPTY_BUFFER, 0, ErrorMapping.valueOf(e));
        chunkQueue.put(new FetchedDataChunk(messages, this, fetchOffset));
    }
}
