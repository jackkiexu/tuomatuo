package com.lami.tuomatuo.mq.jafka.message.compress;

import com.sohu.jafka.utils.Closer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xjk on 2016/10/9.
 */
public abstract class CompressionFacade implements Closeable {

    protected InputStream inputStream;

    protected OutputStream outputStream;

    public CompressionFacade(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public int read(byte[] b) throws IOException{
        return inputStream.read(b);
    }

    public void write(byte[] b) throws Exception{
        outputStream.write(b);
    }

    public void close() {
        Closer.closeQuietly(inputStream);
        Closer.closeQuietly(outputStream);
    }
}
