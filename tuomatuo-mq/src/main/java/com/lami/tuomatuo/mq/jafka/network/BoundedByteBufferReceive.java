package com.lami.tuomatuo.mq.jafka.network;

import com.sohu.jafka.network.InvalidRequestException;
import com.sohu.jafka.utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by xjk on 10/1/16.
 */
public class BoundedByteBufferReceive extends AbstractTransmission implements Receive {

    ByteBuffer sizeBuffer = ByteBuffer.allocate(4);

    private ByteBuffer contentBuffer = null;

    private int maxRequestSize;

    public BoundedByteBufferReceive() {
        this(Integer.MAX_VALUE);
    }

    public BoundedByteBufferReceive(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public ByteBuffer buffer() {
        expectComplete();
        return contentBuffer;
    }

    public int readFrom(ReadableByteChannel channel) throws IOException {
        expectComplete();;
        int read = 0;
        if(sizeBuffer.remaining() > 0){
            read += Utils.read(channel, sizeBuffer);
        }

        if(contentBuffer == null && !sizeBuffer.hasRemaining()) {
            sizeBuffer.rewind();
            int size = sizeBuffer.getInt();
            if (size < 0) {
                throw new InvalidRequestException(size + " is not a valid request size");
            }

            if (size > maxRequestSize) throw new InvalidRequestException(
                    "Request of length " + size + "is not valid, it is larger than the maximum size of " + maxRequestSize + "bytes"
            );
            contentBuffer = byteBufferAllocate(size);
        }

        if(contentBuffer != null){
            read = Utils.read(channel, contentBuffer);
            if(!contentBuffer.hasRemaining()){
                contentBuffer.rewind();
                setCompleted();
            }
        }
        return read;
    }

    public int readCompletely(ReadableByteChannel channel) throws IOException {
        int read = 0;
        while(!complete()){
            read += readFrom(channel);
            return read;
        }
        return 0;
    }

    private ByteBuffer byteBufferAllocate(int size){
        ByteBuffer buffer = null;
        try {
            buffer = ByteBuffer.allocate(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
