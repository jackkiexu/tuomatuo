package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.*;
import com.lami.tuomatuo.mq.base.netty.channel.socket.DefaultServerSocketChannelConfig;
import com.lami.tuomatuo.mq.base.netty.channel.socket.ServerSocketChannel;
import com.lami.tuomatuo.mq.base.netty.channel.socket.ServerSocketChannelConfig;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioServerSocketChannel  extends AbstractServerChannel implements ServerSocketChannel {

    private static final Logger logger = Logger.getLogger(NioServerSocketChannel.class);

    java.nio.channels.ServerSocketChannel socket;
    ServerSocketChannelConfig config;

    NioServerSocketChannel(ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {
        super(factory, pipeline, sink);
        try {
            socket = java.nio.channels.ServerSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ChannelException("Failed to open a server socket");
        }

        try {
            socket.socket().setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.info("Failed to close a partially initialiaed socket");
            }
            throw new ChannelException("Failed to set the server socket timeout");
        }

        config = new DefaultServerSocketChannelConfig(socket.socket());
        Channels.fireChannelOpen(this);
    }

    public ServerSocketChannelConfig getConfig() {
        return config;
    }


    public boolean isBound() {
        return isOpen() && socket.socket().isBound();
    }

    public boolean isConnected() {
        return false;
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)socket.socket().getLocalSocketAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public boolean setClosed() {
        return super.setClosed();
    }

    @Override
    protected ChannelFuture getSucceededFuture() {
        return super.getSucceededFuture();
    }
}
