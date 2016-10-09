package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.socket.SocketChannelConfig;

/**
 * Created by xujiankang on 2016/9/27.
 */
public interface NioSocketChannelConfig extends SocketChannelConfig {

    int getWriteSpinCount();

    /**
     * The maximum loop count for a write operation util
     * WriteAbleByteChannel returns a non-zero value
     * It is similar to what a spin lock is for in concurrency programming
     * It improves memory utilization and write throughput significantly
     * @param writeSpinCount
     */
    void setWriteSpinCount(int writeSpinCount);

    ReceiveBufferSizePredictor getReceiveBufferSizePredictor();

    void setReceiveBufferSizePredictor(ReceiveBufferSizePredictor predictor);

    boolean isReadWriteFair();

    void setReadWriteFair(boolean fair);

}
