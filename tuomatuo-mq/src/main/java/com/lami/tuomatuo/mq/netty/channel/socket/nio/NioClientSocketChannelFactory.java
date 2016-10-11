package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.ChannelPipeline;
import com.lami.tuomatuo.mq.netty.channel.ChannelSink;
import com.lami.tuomatuo.mq.netty.channel.socket.ClientSocketChannelFactory;
import com.lami.tuomatuo.mq.netty.channel.socket.ServerSocketChannel;

import java.util.concurrent.Executor;

/**
 * Created by xjk on 2016/9/28.
 */
public class NioClientSocketChannelFactory implements ClientSocketChannelFactory {

    Executor bossExecutor;
    private ChannelSink sink;

    public NioClientSocketChannelFactory(
            Executor bossExecutor, Executor workerExecutor) {
        this(bossExecutor, workerExecutor, Runtime.getRuntime().availableProcessors());
    }

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

        this.bossExecutor = bossExecutor;
        sink = new NioClientSocketPipelineSink(bossExecutor, workerExecutor, workerCount);
    }

    public ServerSocketChannel newChannel(ChannelPipeline pipeline) {
        return new NioServerSocketChannel(this, pipeline, sink);
    }
}
