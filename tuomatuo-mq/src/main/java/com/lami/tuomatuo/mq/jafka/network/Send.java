package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;

/**
 * Created by xjk on 9/25/16.
 */
public interface Send extends Transmission {

    int writeTo(java.nio.channels.GatheringByteChannel channel) throws IOException;

    int writeCompletely(java.nio.channels.GatheringByteChannel channel) throws IOException;

}
