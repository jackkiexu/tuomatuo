package com.lami.tuomatuo.mq.netty.channel;

import java.net.SocketAddress;

/**
 * Created by xujiankang on 2016/9/27.
 */
public abstract class AbstractServerChannel extends AbstractChannel {


    protected AbstractServerChannel(
            ChannelFactory factory,
            ChannelPipeline pipeline,
            ChannelSink sink) {
        super(null, factory, pipeline, sink);
    }

    public AbstractServerChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return getUnsupportedOperationFuture();
    }

    @Override
    public ChannelFuture disconnect() {
        return getUnsupportedOperationFuture();
    }

    @Override
    public int getInterestOps() {
        return OP_NONE;
    }

    @Override
    public ChannelFuture setInterestOps(int interestOps) {
        return getUnsupportedOperationFuture();
    }

    @Override
    protected void setInterestOpsNow(int interestOps) {
        // Ignore.
    }

    @Override
    public ChannelFuture write(Object message) {
        return getUnsupportedOperationFuture();
    }

    @Override
    public ChannelFuture write(Object message, SocketAddress remoteAddress) {
        return getUnsupportedOperationFuture();
    }
}
