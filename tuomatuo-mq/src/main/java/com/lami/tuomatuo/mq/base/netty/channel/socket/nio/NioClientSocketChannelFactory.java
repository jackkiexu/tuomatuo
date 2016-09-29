package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.Channel;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipeline;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelSink;
import com.lami.tuomatuo.mq.base.netty.channel.socket.ClientSocketChannelFactory;
import com.lami.tuomatuo.mq.base.netty.channel.socket.SocketChannel;

import java.util.concurrent.Executor;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioClientSocketChannelFactory implements ClientSocketChannelFactory {

    private ChannelSink sink;

    public NioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, int workerCount){
        if(bossExecutor == null){
            throw new NullPointerException("bossExecutor");
        }
        if(workerExecutor == null){
            throw new NullPointerException("workerExecutor");
        }
        if(workerCount <= 0){
            throw new IllegalArgumentException("wokerCount(" + workerCount + ")" + " must be a positive integer" );
        }
        sink = new NioClientSocketPipelineSink(bossExecutor, workerExecutor, workerCount);
    }

    public SocketChannel newChannel(ChannelPipeline pipeline) {
        return null;
    }

    public Channel newChannel(io.netty.channel.ChannelPipeline pipline) {
        return null;
    }
}
