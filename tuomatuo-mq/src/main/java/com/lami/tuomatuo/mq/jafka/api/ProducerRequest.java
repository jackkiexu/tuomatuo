package com.lami.tuomatuo.mq.jafka.api;

import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.common.annotations.ServerSide;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.network.Request;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.nio.ByteBuffer;

/**
 * message producer request
 * <p>
 *     request format:
 *     <pre>
 *         topic + partition + messageSize + message
 *         +++++++++++++++++++++++++++++++++++++++++
 *         topic: size(2bytes) + data(utf-8 bytes)
 *         partition: int(4 bytes)
 *         messageSize: int(4 bytes)
 *         message: bytes
 *     </pre>
 *
 * </p>
 *
 * Created by xjk on 2016/11/4.
 */
@ClientSide
@ServerSide
public class ProducerRequest implements Request {

    public static final int RandomPartition = -1;

    public static ProducerRequest readFrom(ByteBuffer buffer){
        String topic = Utils.readShortString(buffer);
        int partition = buffer.getInt();
        int messageSetSize = buffer.getInt();
        ByteBuffer messageSetBuffer = buffer.slice();
        messageSetBuffer.limit(messageSetSize);
        buffer.position(buffer.position() + messageSetSize);
        return new ProducerRequest(topic, partition, new ByteBufferMessageSet(messageSetBuffer));
    }

    ByteBufferMessageSet messages;

    public int partition;

    public String topic;

    public ProducerRequest(String topic, int partition, ByteBufferMessageSet messages){
        this.topic = topic;
        this.partition = partition;
        this.messages = messages;
    }

    public ByteBufferMessageSet getMessages(){
        return messages;
    }

    public int getPartition(){
        return partition;
    }

    public RequestKeys getRequestKey() {
        return RequestKeys.Produce;
    }

    public int getSizeInBytes(){
        return (int)(Utils.caculateShortString(topic) + 4 + 4 + messages.getSizeInBytes());
    }

    public String getTopic(){
        return topic;
    }

    public int getTranslatedPartition(PartitionChooser chooser){
        if(partition == RandomPartition){
            return chooser.choosePartition(topic);
        }
        return partition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProducerRequest (");
        sb.append(topic + ", ").append(partition + ", ");
        sb.append(messages.getSizeInBytes()).append(" ) ");
        return sb.toString();
    }

    public void writeTo(ByteBuffer buffer) {
        Utils.writeShortString(buffer, topic);
        buffer.putInt(partition);
        buffer.putInt(messages.serialized().limit());
        buffer.put(messages.serialized());
        messages.serialized().rewind();
    }

}
