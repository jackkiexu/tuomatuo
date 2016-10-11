package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.ChannelPipeline;
import com.lami.tuomatuo.mq.netty.channel.ChannelSink;
import com.lami.tuomatuo.mq.netty.channel.socket.ServerSocketChannel;
import com.lami.tuomatuo.mq.netty.channel.socket.ServerSocketChannelFactory;

import java.util.concurrent.Executor;

/**
 * Created by xjk on 2016/9/28.
 */
public class NioServerSocketChannelFactory implements ServerSocketChannelFactory {

    Executor bossExecutor;
    private ChannelSink sink;

    public NioServerSocketChannelFactory(
            Executor bossExecutor, Executor workerExecutor) {
        this(bossExecutor, workerExecutor, Runtime.getRuntime().availableProcessors());
    }

    public NioServerSocketChannelFactory(
            Executor bossExecutor, Executor workerExecutor,
            int workerCount) {
        if (bossExecutor == null) {
            throw new NullPointerException("bossExecutor");
        }
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        if (workerCount <= 0) {
            throw new IllegalArgumentException(
                    "workerCount (" + workerCount + ") " +
                            "must be a positive integer.");
        }
        this.bossExecutor = bossExecutor;
        sink = new NioServerSocketPipelineSink(workerExecutor, workerCount);
    }

    public ServerSocketChannel newChannel(ChannelPipeline pipeline) {
        return new NioServerSocketChannel(this, pipeline, sink); // sink that created in Construct
    }
}
