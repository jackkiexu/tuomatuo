package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;

/**
 * Created by xjk on 9/25/16.
 */
public abstract class AbstractSend extends AbstractTransmission implements Send {

    public int writeCompletely(GatheringByteChannel channel) throws IOException {
        int written = 0;
        while (!complete()){
            written = writeTo(channel);
        }
        return written;
    }
}
