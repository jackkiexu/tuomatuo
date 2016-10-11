package com.lami.tuomatuo.mq.jafka.api;

import com.lami.tuomatuo.mq.jafka.network.Request;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by xjk on 2016/10/8.
 */
@Data
public class OffsetRequest implements Request {

    public static final String SmallestTimeString = "smallest";

    public static final String LargestTimeString = "largest";

    public static final long LatestTime = -1l;

    public static final long EarliesTime = -2l;

    private String topic;

    private int partition;

    private long time;

    private int maxNumOffsets;

    public OffsetRequest(String topic, int partition, long time, int maxNumOffsets) {
        this.topic = topic;
        this.partition = partition;
        this.time = time;
        this.maxNumOffsets = maxNumOffsets;
    }

    public RequestKeys getRequestKey() {
        return RequestKeys.Offset;
    }

    public int getSizeInBytes() {
        return Utils.caculateShortString(topic) + 4 + 8 + 4;
    }

    public void writeTo(ByteBuffer buffer) {
        Utils.writeShortString(buffer, topic);
        buffer.putInt(partition);
        buffer.putLong(time);
        buffer.putInt(maxNumOffsets);
    }

    public static OffsetRequest readFrom(ByteBuffer buffer){
        String topic = Utils.readShortString(buffer);
        int partition = buffer.getInt();
        long offset = buffer.getLong();
        int maxNumOffsets = buffer.getInt();
        return new OffsetRequest(topic, partition, offset, maxNumOffsets);
    }

    public static ByteBuffer serializeOffsetArray(List<Long> offsets){
        int size = 4 + 8 * offsets.size();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putInt(offsets.size());
        for(int i = 0; i < offsets.size(); i++){
            buffer.putLong(offsets.get(i));
        }
        buffer.rewind();
        return buffer;
    }

    public static ByteBuffer serializeOffsetArray(long[] offsets){
        int size = 4 + 8 * offsets.length;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putInt(offsets.length);
        for(int i = 0; i < offsets.length; i++){
            buffer.putLong(offsets[i]);
        }

        buffer.rewind();
        return buffer;
    }

    public static long[] deserializeOffsetArray(ByteBuffer buffer){
        int size = buffer.getInt();
        long[] offsets = new long[size];
        for(int i = 0; i < size; i++){
            offsets[i] = buffer.getLong();
        }
        return offsets;
    }

}
