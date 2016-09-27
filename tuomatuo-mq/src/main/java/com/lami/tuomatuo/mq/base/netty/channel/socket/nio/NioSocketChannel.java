package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.*;
import com.lami.tuomatuo.mq.base.netty.channel.socket.SocketChannel;
import com.lami.tuomatuo.mq.base.netty.channel.socket.SocketChannelConfig;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by xujiankang on 2016/9/27.
 */
public abstract class NioSocketChannel extends AbstractChannel implements SocketChannel {

    SocketChannel socket;
    private NioSocketChannelConfig config;

    Queue<MessageEvent> writeBuffer = new ConcurrentLinkedQueue<MessageEvent>();
    MessageEvent currentWriteEvent;
    int currentWriteIndex;

    public NioSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, SocketChannel socket) {
        super(parent, factory, pipeline, sink);
        this.socket = socket;
//        config = ne
    }

    public SocketChannelConfig getConfig() {
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
