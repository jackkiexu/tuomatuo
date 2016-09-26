package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xjk on 9/25/16.
 */
public class MessageSetSend extends AbstractSend {

    private long sent = 0;
    private long size;
    private final ByteBuffer header = ByteBuffer.allocate(6);



    public int writeTo(GatheringByteChannel channel) throws IOException {
        return 0;
    }
}
