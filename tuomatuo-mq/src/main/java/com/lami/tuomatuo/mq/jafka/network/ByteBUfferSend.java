package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xjk on 9/25/16.
 */
public class ByteBufferSend extends AbstractSend{

    ByteBuffer buffer;

    public ByteBufferSend(int size) {
        this(ByteBuffer.allocate(size));
    }

    public ByteBufferSend(ByteBuffer buffers) {
        this.buffer = buffers;
    }

    public ByteBuffer getBuffers() {
        return buffer;
    }

    public int writeTo(GatheringByteChannel channel) throws IOException {
        expectComplete();
        int written = 0;
        written += channel.write(buffer);
        if(!buffer.hasRemaining()){
            setCompleted();
        }
        return written;
    }
}
