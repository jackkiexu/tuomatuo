package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.common.InvalidMessageSizeException;
import com.lami.tuomatuo.mq.jafka.common.MessageSizeTooLargeException;
import com.lami.tuomatuo.mq.jafka.utils.IteratorTemplate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Iterator;

/**
 * A sequence of messages stored in a byte buffer
 *
 * There are two way to create a ByteBufferMessageSet
 *
 * Option 1 : From a ByteBuffer which already contains the serialized
 * message set. Consumers will use this method
 *
 * Option 2: Give it a list of messages along with instructions relating to serialization format. Producers will use this method
 *
 * Created by xjk on 2016/10/9.
 */
public class ByteBufferMessageSet extends MessageSet {

    public ByteBuffer buffer;
    public long initialOffset;
    public ErrorMapping errorCode;

    long validByteCount = -1l;
    long shallowValidByteCount = -1l;

    private long validBytes;

    public ByteBufferMessageSet(ByteBuffer buffer) {
        this(buffer, 0L, ErrorMapping.NoError);
    }

    public ByteBufferMessageSet(ByteBuffer buffer, long initialOffset, ErrorMapping errorCode) {
        this.buffer = buffer;
        this.initialOffset = initialOffset;
        this.errorCode = errorCode;
        this.validBytes = shallowValidBytes();
    }

    public ByteBufferMessageSet(CompressionCodec compressionCodec, Message... messages){
    }

    private long shallowValidBytes(){
        if(shallowValidByteCount < 0){
            Iterator<MessageAndOffset> iter = this.internalIterator(true);
            while(iter.hasNext()){
                shallowValidByteCount = iter.next().offset;
            }
        }

        if(shallowValidByteCount < initialOffset){
            return 0;
        }else{
            return shallowValidByteCount - initialOffset;
        }
    }

    public long getInitialOffset(){
        return initialOffset;
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }

    public ErrorMapping getErrorCode(){
        return errorCode;
    }

    public ByteBuffer serialized(){
        return buffer;
    }

    public Iterator<MessageAndOffset> iterator(){
        return internalIterator(false);
    }

    public Iterator<MessageAndOffset> internalIterator(boolean isShallow){
        return new Iter(isShallow);
    }

    public boolean hasNext() {
        return false;
    }

    public MessageAndOffset next() {
        return null;
    }

    /**
     *
     * @return
     */
    public long getValidBytes(){
        return validBytes;
    }

    public void remove() {

    }

    @Override
    public long writeTo(GatheringByteChannel channel, long offset, long maxSize) throws IOException {
        buffer.mark();
        int written = channel.write(buffer);
        buffer.reset();
        return written;
    }

    class Iter extends IteratorTemplate<MessageAndOffset>{

        boolean isShallow;
        ByteBuffer topIter = buffer.slice();
        long currValidBytes = initialOffset;
        Iterator<MessageAndOffset> innerIter = null;
        long lastMessageSize = 0l;

        public Iter(boolean isShallow) {
            this.isShallow = isShallow;
        }

        private boolean innerDone(){
            return innerIter == null || !innerIter.hasNext();
        }

        private MessageAndOffset makeNextOuter(){
            if(topIter.remaining() < 4) return allDone();
            int size = topIter.getInt();
            lastMessageSize = size;
            if(size < 0 || topIter.remaining() < size){
                if(currValidBytes == initialOffset||size<0){
                    throw new InvalidMessageSizeException("invalid message size: " + size + "only received bytes: " +
                    topIter.remaining() + " at " + currValidBytes + "( possible causes (1) single message larger than "
                    + " the fetch size; (2) log corruption");
                }
                return allDone();
            }

            ByteBuffer message = topIter.slice();
            message.limit(size);
            topIter.position(topIter.position() + size);
            Message newMessage = new Message(message);
            if(isShallow){
                currValidBytes += 4 + size;
                return new MessageAndOffset(newMessage, currValidBytes);
            }
            if(newMessage.compressionCodec() == CompressionCodec.NoCompressionCodec){

            }

            return makeNext();
        }

        @Override
        protected MessageAndOffset makeNext() {
            if(isShallow){
                return makeNextOuter();
            }
            if(innerDone()) return makeNextOuter();
            MessageAndOffset messageAndOffset = innerIter.next();
            if(!innerIter.hasNext()){
                currValidBytes += 4 + lastMessageSize;
            }

            return new MessageAndOffset(messageAndOffset.message, currValidBytes);
        }

    }

    public long getSizeInBytes(){
        return buffer.limit();
    }

    public void verifyMessageSize(int maxMessageSize){
        Iterator<MessageAndOffset> shallowIter = internalIterator(true);
        while(shallowIter.hasNext()){
            MessageAndOffset messageAndOffset = shallowIter.next();
            int payloadsize = messageAndOffset.message.payloadSize();
            if(payloadsize > maxMessageSize){
                throw new MessageSizeTooLargeException("payload size of  " + payloadsize + " larger than " + maxMessageSize);
            }
        }
    }
}
