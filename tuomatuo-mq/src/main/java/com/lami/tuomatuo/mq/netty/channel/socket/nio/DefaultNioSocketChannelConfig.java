package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.socket.DefaultSocketChannelConfig;
import com.lami.tuomatuo.mq.netty.util.ConvertUtil;

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

    @Override
    protected boolean setOption(String key, Object value) {
        if(super.setOption(key, value)){
            return true;
        }

        if(key.equals("readWriteFair")){
            setReadWriteFair(ConvertUtil.toBoolean(value));
        }else if(key.equals("writeSpinCount")){
            setWriteSpinCount(ConvertUtil.toInt(value));
        }else if(key.equals("receiveBufferSizePredictor")){
            setReceiveBufferSizePredictor((ReceiveBufferSizePredictor)value);
        }else{
            return false;
        }
        return true;
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
        this.predictor = predictor;
    }

    public boolean isReadWriteFair() {
        return false;
    }

    public void setReadWriteFair(boolean fair) {
        this.readWriterFair = readWriterFair;
    }
}
