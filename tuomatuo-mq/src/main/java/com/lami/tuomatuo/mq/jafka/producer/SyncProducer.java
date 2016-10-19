package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.common.annotations.ThreadSafe;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xjk on 2016/10/19.
 */
@ThreadSafe
public class SyncProducer implements Closeable {
    public void close() throws IOException {

    }
}
