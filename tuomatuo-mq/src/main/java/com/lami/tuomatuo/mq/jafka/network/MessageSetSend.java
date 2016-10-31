package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.message.MessageSet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xjk on 9/25/16.
 */
public class MessageSetSend extends AbstractSend {

    private long sent = 0;
    private long size;
    private final ByteBuffer header = ByteBuffer.allocate(6);

    public MessageSet messageSet;

    public ErrorMapping errorCode;

    public MessageSetSend(MessageSet messageSet, ErrorMapping errorCode) {
        super();
        this.messageSet = messageSet;
        this.errorCode = errorCode;
        this.size = messageSet.getSizeInBytes();
        header.putInt((int)(size + 2));
        header.putShort(errorCode.code);
        header.rewind();
    }

    public MessageSetSend(MessageSet messageSet) {
        this(messageSet, ErrorMapping.NoError);
    }

    public MessageSetSend(){
        this(MessageSet.Empty);
    }

    public int writeTo(GatheringByteChannel channel) throws IOException {
        expectComplete();
        int written = 0;
        if(header.hasRemaining()){
            written += channel.write(header);
        }
        if(!header.hasRemaining()){
            int fileBytesSent = (int)messageSet.writeTo(channel, sent, size - sent);
            written += fileBytesSent;
            sent += fileBytesSent;
        }
        if(sent >= size){
            setCompleted();
        }
        return written;
    }

    public int getSendSize(){
        return (int)size + header.capacity();
    }
}
