package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xjk on 2016/10/8.
 */
public class BoundedByteBufferSend extends AbstractSend{

    ByteBuffer buffer;

    private ByteBuffer sizeBuffer = ByteBuffer.allocate(4);

    public BoundedByteBufferSend(ByteBuffer buffer) {
        this.buffer = buffer;
        sizeBuffer.putInt(buffer.limit());
        sizeBuffer.rewind();
    }

    public BoundedByteBufferSend(int size) {
        this(ByteBuffer.allocate(size));
    }

    public BoundedByteBufferSend(Request request) {
        this(request.getSizeInBytes() + 2);
        buffer.putShort((short) request.getRequestKey().value);
        request.writeTo(buffer);
        buffer.rewind();
    }

    public ByteBuffer getBuffer(){
        return buffer;
    }

    public int writeTo(GatheringByteChannel channel) throws IOException {
        expectIncomplete();
        int written = 0;
        // try to write the size if we haven't already
        if(sizeBuffer.hasRemaining()) written += channel.write(sizeBuffer);
        // try to write the actual buffer itself
        if(!sizeBuffer.hasRemaining() && buffer.hasRemaining()) written += channel.write(buffer);
        // if we are done, mark it off
        if(!buffer.hasRemaining()){
            setCompleted();
        }
        return written;
    }
}
