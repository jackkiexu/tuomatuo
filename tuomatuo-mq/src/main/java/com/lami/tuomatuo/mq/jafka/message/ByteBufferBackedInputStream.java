package com.lami.tuomatuo.mq.jafka.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/10/9.
 */
public class ByteBufferBackedInputStream extends InputStream {

    ByteBuffer buffer;

    public ByteBufferBackedInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        return buffer.hasRemaining()? (buffer.get()& 0xFF) : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(buffer.hasRemaining()){
            int realLen = Math.min(len, buffer.remaining());
            buffer.get(b, off, realLen);
            return realLen;
        }
        return -1;
    }
}
