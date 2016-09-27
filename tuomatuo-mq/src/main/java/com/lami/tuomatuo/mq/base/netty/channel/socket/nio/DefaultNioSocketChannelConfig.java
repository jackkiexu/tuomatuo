package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.socket.DefaultSocketChannelConfig;

import java.net.Socket;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class DefaultNioSocketChannelConfig extends DefaultSocketChannelConfig implements NioSocketChannelConfig{

    private volatile ReceiveBufferSizePredictor predictor = new DefaultReceiveBufferSizePredictor();
    private volatile int writeSpinCount = 16;
    private volatile boolean readWriterFair;


    public DefaultNioSocketChannelConfig(Socket socket) {
        super(socket);
    }

    public int getWriteSpinCount() {
        return writeSpinCount;
    }

    public void setWriteSpinCount(int writeSpinCount) {
        if(writeSpinCount <= 0) throw new IllegalArgumentException("writeSpinCount must be a positive integer");
    }

    public ReceiveBufferSizePredictor getReceiveBufferSizePredictor() {
        return predictor;
    }

    public void setReceiveBufferSizePredictor(ReceiveBufferSizePredictor predictor) {
        if(predictor == null) throw new NullPointerException();
    }

    public boolean isReadWriteFair() {
        return false;
    }

    public void setReadWriteFair(boolean fair) {

    }
}
