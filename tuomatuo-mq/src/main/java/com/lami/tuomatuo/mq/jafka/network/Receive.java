package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by xjk on 9/25/16.
 */
public interface Receive extends Transmission {

    ByteBuffer buffer();

    int readFrom(ReadableByteChannel channel) throws IOException;

    int readCompletely(ReadableByteChannel channel) throws IOException;

}
