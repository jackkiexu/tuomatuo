package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;

/**
 * Created by xjk on 2016/10/31.
 */
public class FetchedDataChunk {

    public ByteBufferMessageSet messages;

    public PartitionTopicInfo topicInfo;

    public long fetchOffset;

    public FetchedDataChunk(ByteBufferMessageSet messages, PartitionTopicInfo topicInfo, long fetchOffset) {
        super();
        this.messages = messages;
        this.topicInfo = topicInfo;
        this.fetchOffset = fetchOffset;
    }
}
