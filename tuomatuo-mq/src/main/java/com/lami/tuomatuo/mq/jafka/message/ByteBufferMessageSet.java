package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.utils.IteratorTemplate;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Created by xujiankang on 2016/10/9.
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

    public void remove() {

    }

    @Override
    public long getSizeInBytes() {
        return 0;
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
