package com.lami.tuomatuo.mq.netty.bootstrap;

import com.lami.tuomatuo.mq.netty.channel.*;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/9/29.
 */
public class ClientBootstrap extends Bootstrap {


    public ClientBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

    public ChannelFuture connect() {
        SocketAddress remoteAddress = (SocketAddress) getOption("remoteAddress");
        if (remoteAddress == null) {
            throw new IllegalStateException("remoteAddress option is not set.");
        }
        SocketAddress localAddress = (SocketAddress) getOption("localAddress");
        return connect(remoteAddress, localAddress);
    }


    public ChannelFuture connect(SocketAddress remoteAddress){
        if(remoteAddress == null){
            throw new NullPointerException("remoteAddress");
        }
        return connect(remoteAddress, null);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress){
        BlockingQueue<ChannelFuture> futureQueue = new LinkedBlockingQueue<ChannelFuture>();
        ChannelPipeline pipeline ;
        try {
            pipeline = getPipelineFactory().getPipeline();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ChannelPipelineException("Failed to initialize a pipeline");
        }

        pipeline.addFirst("connector", new Connector(remoteAddress, localAddress, futureQueue));

        getFactory().newChannel(pipeline);

        // Wait until the future is available.
        ChannelFuture future = null;

        do {
            try {
                future = futureQueue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Ignore exception
                e.printStackTrace();
            }
        }while(future == null);

        pipeline.remove(pipeline.get("connector"));
        return future;
    }

    @ChannelPipelineCoverage("one")
    private class Connector extends SimpleChannelHandler{
        private SocketAddress localAddress;
        private BlockingQueue<ChannelFuture> futureQueue;
        private SocketAddress remoteAddress;
        private volatile boolean finished = false;

        public Connector(SocketAddress localAddress, SocketAddress remoteAddress, BlockingQueue<ChannelFuture> futureQueue) {
            this.localAddress = localAddress;
            this.futureQueue = futureQueue;
            this.remoteAddress = remoteAddress;
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelOpen(ctx, e);

            // Apply options
            e.getChannel().getConfig().setOptions(getOptions());

            // Bind or connect
            if(localAddress != null){
                e.getChannel().bind(localAddress);
            }else{
                futureQueue.offer(e.getChannel().connect(remoteAddress));
                finished = true;
            }
        }

        @Override
        public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelBound(ctx, e);

            // Connected if not connected yet
            if(localAddress != null){
                futureQueue.offer(e.getChannel().connect(remoteAddress));
                finished = true;
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            super.exceptionCaught(ctx, e);
            if(!finished){
                e.getChannel().close();
                futureQueue.offer(Channels.failedFuture(e.getChannel(), e.getCause()));
                finished = true;
            }
        }
    }
}
