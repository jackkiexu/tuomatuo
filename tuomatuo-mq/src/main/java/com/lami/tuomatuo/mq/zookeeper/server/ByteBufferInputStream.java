package com.lami.tuomatuo.mq.zookeeper.server;


import org.apache.jute.BinaryInputArchive;
import org.apache.jute.Record;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ByteBufferInputStream extends InputStream{

    public ByteBuffer bb;

    public ByteBufferInputStream(ByteBuffer bb) {
        this.bb = bb;
    }



    @Override
    public int read() throws IOException {
        if(bb.remaining() == 0){
            return -1;
        }
        return bb.get() & 0xff;
    }

    @Override
    public int available() throws IOException {
        return bb.remaining();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(bb.remaining() == 0){
            return -1;
        }
        if(len > bb.remaining()){
            len = bb.remaining();
        }
        bb.get(b, off, len);
        return len;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public long skip(long n) throws IOException {
        if(n < 0L){
            return 0;
        }
        n = Math.min(n, bb.remaining());
        bb.position(bb.position() + (int)n);
        return n;
    }

    static public void byteBuffer2Record(ByteBuffer bb, Record record) throws IOException{
        BinaryInputArchive ia;
        ia = BinaryInputArchive.getArchive(new ByteBufferInputStream(bb));
        record.deserialize(ia, "request");
    }
}
