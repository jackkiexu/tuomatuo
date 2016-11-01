package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
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

    public Iterator<MessageAndOffset> internalIterator(boolean isShallow){
        return new Iter(isShallow);
    }

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

    public boolean hasNext() {
        return false;
    }

    public MessageAndOffset next() {
        return null;
    }

    public long getValidBytes(){
        return validBytes;
    }

    public void remove() {

    }

    @Override
    public long getSizeInBytes() {
        return 0;
    }

    @Override
    public long writeTo(GatheringByteChannel channel, long offset, long maxSize) throws IOException {
        return 0;
    }

    public Iterator<MessageAndOffset> iterator() {
        return null;
    }

    class Iter extends IteratorTemplate<MessageAndOffset>{

        boolean isShallow;
        ByteBuffer topIter = buffer.slice();

        public Iter(boolean isShallow) {
            this.isShallow = isShallow;
        }

        @Override
        protected MessageAndOffset makeNext() {
            return null;
        }

        public Iterator<MessageAndOffset> iterator() {
            return null;
        }
    }
}
