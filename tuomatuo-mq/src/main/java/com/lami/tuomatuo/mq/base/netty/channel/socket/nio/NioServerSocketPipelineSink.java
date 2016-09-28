package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.AbstractChannelSink;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelEvent;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioServerSocketPipelineSink extends AbstractChannelSink {

    private final NioWorker[] workers;
    private final int id = nextId.incrementAndGet();
    private static final AtomicInteger nextId = new AtomicInteger();

    NioServerSocketPipelineSink(Executor workerExecutor, int workerCount) {
        workers = new NioWorker[workerCount];
        for (int i = 0; i < workers.length; i ++) {
            workers[i] = new NioWorker(id, i + 1, workerExecutor);
        }
    }

    public void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception {

    }
}
