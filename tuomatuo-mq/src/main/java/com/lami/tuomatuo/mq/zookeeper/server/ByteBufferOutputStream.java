package com.lami.tuomatuo.mq.zookeeper.server;

import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ByteBufferOutputStream extends OutputStream {

    public ByteBuffer bb;

    public ByteBufferOutputStream(ByteBuffer bb) {
        this.bb = bb;
    }

    @Override
    public void write(int b) throws IOException {
        bb.put((byte)b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        bb.put(b);
    }


    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        bb.put(b, off, len);
    }

    static public void record2ByteBuffer(Record record, ByteBuffer bb) throws IOException{
        BinaryOutputArchive oa;
        oa = BinaryOutputArchive.getArchive(new ByteBufferOutputStream(bb));
        record.serialize(oa, "request");
    }
}
