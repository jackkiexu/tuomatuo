package com.lami.tuomatuo.mq.netty.channel.socket.nio;

/**
 * Created by xjk on 2016/9/27.
 */
public interface ReceiveBufferSizePredictor {

    int nextReceiveBufferSize();

    void previousReceiveBufferSize(int previousReceiveBufferSize);
}
