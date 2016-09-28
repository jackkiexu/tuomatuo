package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.ChannelFuture;

import java.util.concurrent.Executor;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioWorker {

    public NioWorker(int bossId, int id, Executor executor) {
    }

    static void write(NioSocketChannel channel) {

    }

    static void setInterestOps(
            NioSocketChannel channel, ChannelFuture future, int interestOps) {

    }

    static void close(NioSocketChannel channel, ChannelFuture future){

    }

    void register(NioSocketChannel channel, ChannelFuture future){

    }
}
