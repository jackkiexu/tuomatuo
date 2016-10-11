package com.lami.tuomatuo.mq.jafka.api;

import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.common.annotations.ServerSide;
import com.lami.tuomatuo.mq.jafka.network.Request;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/10/9.
 */
@ClientSide
@ServerSide
public class FetchRequest implements Request {

    private String topic;

    private int partition;

    private long offset;

    private int maxSize;

    public FetchRequest(String topic, int partition, long offset, int maxSize) {
        this.topic = topic;
        if(topic == null){
            throw new IllegalArgumentException("no topic");
        }

        this.partition = partition;
        this.offset = offset;
        this.maxSize = maxSize;
    }

    public RequestKeys getRequestKey() {
        return RequestKeys.Fetch;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public int getMaxSize() {
        return maxSize;
    }


    public void writeTo(ByteBuffer buffer) {
        Utils.writeShortString(buffer, topic);
        buffer.putInt(partition);
        buffer.putLong(offset);
        buffer.putInt(maxSize);
    }

    public int getSizeInBytes() {
        return Utils.caculateShortString(topic) + 4 + 8 + 4;
    }

    @Override
    public String toString() {
        return "FetchRequest{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", maxSize=" + maxSize +
                '}';
    }

    /**
     * Read a fetch request from buffer
     *
     * @param buffer
     * @return
     */
    public static FetchRequest readFrom(ByteBuffer buffer){
        String topic = Utils.readShortString(buffer);
        int partition = buffer.getInt();
        long offset = buffer.getLong();
        int size = buffer.getInt();
        return new FetchRequest(topic, partition, offset, size);
    }
}
