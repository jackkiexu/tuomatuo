package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.socket.SocketChannel;
import com.lami.tuomatuo.mq.netty.channel.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by xujiankang on 2016/9/27.
 */
public abstract class NioSocketChannel extends AbstractChannel implements SocketChannel {

    java.nio.channels.SocketChannel socket;
    private NioSocketChannelConfig config;

    Queue<MessageEvent> writeBuffer = new ConcurrentLinkedQueue<MessageEvent>();
    MessageEvent currentWriteEvent;
    int currentWriteIndex;

    public NioSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, java.nio.channels.SocketChannel socket) {
        super(parent, factory, pipeline, sink);
        this.socket = socket;
        config = new DefaultNioSocketChannelConfig(socket.socket());
    }

    abstract NioWorker getWorker();
    abstract void setWork(NioWorker worker);

    public NioSocketChannelConfig getConfig(){
        return config;
    }

    public ChannelPipeline getPipline() {
        return null;
    }

    public boolean isBound() {
        return isOpen() && socket.socket().isBound();
    }

    public boolean isConnected(){
        return isOpen() && socket.socket().isConnected();
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) socket.socket().getLocalSocketAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) socket.socket().getRemoteSocketAddress();
    }

    @Override
    public boolean setClosed() {
        return super.setClosed();
    }

    @Override
    protected void setInterestOpsNow(int interestOps) {
        super.setInterestOpsNow(interestOps);
    }

    @Override
    protected ChannelFuture getSucceededFuture() {
        return super.getSucceededFuture();
    }

    @Override
    public ChannelFuture write(Object message, SocketAddress remoteAddress) {
        if(remoteAddress == null || remoteAddress.equals(getRemoteAddress())){
            return super.write(message);
        }
        return getUnsupportedOperationFuture();
    }
}
