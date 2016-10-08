package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xujiankang on 2016/10/8.
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


    public int writeTo(GatheringByteChannel channel) throws IOException {
        return 0;
    }
}
