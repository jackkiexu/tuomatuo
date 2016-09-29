package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.Channel;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipeline;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelSink;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioAcceptedSocketChannel extends NioSocketChannel {

    private NioWorker worker;

    public NioAcceptedSocketChannel( ChannelFactory factory, ChannelPipeline pipeline, Channel parent, ChannelSink sink, java.nio.channels.SocketChannel socket) {
        super(parent, factory, pipeline, sink, socket);
    }

    public NioAcceptedSocketChannel(ChannelFactory factory, ChannelPipeline pipeline, Channel parent, ChannelSink sink, java.nio.channels.SocketChannel socket, NioWorker nioWorker) {
        super(parent, factory, pipeline, sink, socket);

        this.worker = worker;
    }

    @Override
    NioWorker getWorker() {
        return worker;
    }

    @Override
    void setWork(NioWorker worker) {
        // woker never changes
        if(this.worker != worker){
            throw  new IllegalStateException("Should not reach here");
        }
    }
}
