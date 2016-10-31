package com.lami.tuomatuo.mq.jafka.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Iterator;

/**
 * Message set helper function
 *
 * Created by xjk on 2016/10/9.
 */
public abstract class MessageSet implements Iterable<MessageAndOffset> {

    public static final MessageSet Empty = new ByteBufferMessageSet(ByteBuffer.allocate(0));

    public static final int LogOverhead = 4;

    public ByteBuffer createByteBuffer(CompressionCodec compressionCodec, Message... messages){
        if(compressionCodec == CompressionCodec.NoCompressionCodec){
            ByteBuffer buffer = ByteBuffer.allocate(messageSetSize(messages));
            for(Message message : messages){
                message.serializeTo(buffer);
            }
            buffer.rewind();
            return buffer;
        }

        if(messages.length == 0){
            ByteBuffer buffer = ByteBuffer.allocate(messageSetSize(messages));
            buffer.rewind();
            return buffer;
        }
        //
        Message message = CompressionUtils.compress(messages, compressionCodec);
        ByteBuffer buffer = ByteBuffer.allocate(message.serializedSize());
        message.serializeTo(buffer);
        buffer.rewind();
        return buffer;
    }

    public static int messageSetSize(Message... messages){
        int size = 0;
        for(Message message : messages){
            size += entrySize(message);
        }
        return size;
    }

    public static int messageSetSize(Iterable<Message> messages){
        int size = 0;
        for(Message message : messages){
            size += entrySize(message);
        }
        return size;
    }

    public static int entrySize(Message message){
        return LogOverhead + message.getSizeInBytes();
    }

    /**
     * Gives the total size of this message set in bytes
     */
    public abstract long getSizeInBytes();

    /**
     * Validate the chacksum of all the messages in the set.
     * Throws an InvalidMessageException if the checksum doesn't match the payload
     * for any message
     */
    public void validate(){
        for(MessageAndOffset messageAndOffset : this){
            if(!messageAndOffset.message.isValid()){
                throw new InvalidMessageException();
            }
        }
    }


    // abstract method
    /**
     * Write the message in this set to the given channel starting at the
     * given offset byt. less than the complete amount may be written, but
     * no more than maxSize can be. The number of bytes written is returned
     */
    public abstract long writeTo(GatheringByteChannel channel, long offset, long maxSize) throws IOException;
}
