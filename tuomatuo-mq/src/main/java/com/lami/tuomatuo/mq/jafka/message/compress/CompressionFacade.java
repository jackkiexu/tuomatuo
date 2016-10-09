package com.lami.tuomatuo.mq.jafka.message.compress;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xujiankang on 2016/10/9.
 */
public abstract class CompressionFacade implements Closeable {

    public void close() throws IOException {

    }
}
