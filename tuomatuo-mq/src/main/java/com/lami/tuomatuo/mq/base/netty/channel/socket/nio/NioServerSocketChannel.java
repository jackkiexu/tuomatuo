package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.*;
import com.lami.tuomatuo.mq.base.netty.channel.socket.ServerSocketChannel;
import com.lami.tuomatuo.mq.base.netty.channel.socket.ServerSocketChannelConfig;

import java.net.InetSocketAddress;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioServerSocketChannel  extends AbstractServerChannel implements ServerSocketChannel {

    protected NioServerSocketChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(factory, pipeline, sink);
    }

    public NioServerSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(parent, factory, pipeline, sink);
    }

    public ServerSocketChannelConfig getConfig() {
        return null;
    }

    public ChannelPipeline getPipline() {
        return null;
    }

    public boolean isBound() {
        return false;
    }

    public boolean isConnected() {
        return false;
    }

    public InetSocketAddress getLocalAddress() {
        return null;
    }

    public InetSocketAddress getRemoteAddress() {
        return null;
    }
}
