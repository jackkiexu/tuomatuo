package com.lami.tuomatuo.mq.netty.bootstrap;

import com.lami.tuomatuo.mq.netty.channel.*;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/9/29.
 */
public class ServerBootstrap extends Bootstrap {

    private volatile ChannelHandler parentHandler;

    public ServerBootstrap() {
        super();
    }

    public ServerBootstrap(ChannelFactory channelFactory) {
        super(channelFactory);
    }

    public ChannelHandler getParentHandler() {
        return parentHandler;
    }
    public void setParentHandler(ChannelHandler parentHandler){
        this.parentHandler = parentHandler;
    }

    public Channel bind(){
        SocketAddress localAddress = (SocketAddress)getOption("localAddress");
        if(localAddress == null){
            throw  new IllegalStateException("localAddress option is not set");
        }
        return bind(localAddress);
    }

    public Channel bind(final SocketAddress localAddress){
        BlockingQueue<ChannelFuture> futureQueue = new LinkedBlockingQueue<ChannelFuture>();

        ChannelPipeline bossPipeline = pipeline();
        bossPipeline.addLast("binder", new Binder(localAddress, futureQueue));

        ChannelHandler parentHandler = getParentHandler();
        if(parentHandler != null){
            bossPipeline.addLast("userHandler", parentHandler);
        }

        Channel channel = this.factory.newChannel(bossPipeline);

        // Wait until the future is available
        ChannelFuture future = null;
        do {
            try {
                future = futureQueue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Ignore
                e.printStackTrace();
            }
        }while(future == null);

        // Wait for the future
        future.awaitUninterruptibly();
        if(!future.isSuccess()){
            future.getChannel().close().awaitUninterruptibly();
            throw new ChannelException("Failed to bind to : " + localAddress + ", " + future.getCause());
        }

        return channel;
    }

    @ChannelPipelineCoverage("one")
    private class Binder extends SimpleChannelHandler{

        private SocketAddress localAddress;
        private BlockingQueue<ChannelFuture> futureQueue;
        private Map<String, Object> childOptions = new HashMap<String, Object>();

        public Binder(SocketAddress localAddress, BlockingQueue<ChannelFuture> futureQueue) {
            this.localAddress = localAddress;
            this.futureQueue = futureQueue;
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            e.getChannel().getConfig().setPiplineFactory(getPipelineFactory());

            // split options into two categories; parent and child

            Map<String, Object> allOptions = getOptions();
            Map<String, Object> parentOptions = new HashMap<String, Object>();
            for(Map.Entry<String, Object> p : allOptions.entrySet()){
                if(p.getKey().startsWith("child.")){
                    childOptions.put(p.getKey().substring(6), e.getValue());
                }else if(!p.getKey().equals("pipelineFactory")){
                    parentOptions.put(p.getKey(), p.getValue());
                }
            }

            // Apply parent options
            e.getChannel().getConfig().setOptions(parentOptions);
            futureQueue.offer(e.getChannel().bind(localAddress));

            super.channelOpen(ctx, e);
        }

        @Override
        public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
            // Apply child options
            e.getChildChannel().getConfig().setOptions(childOptions);
            super.childChannelOpen(ctx, e);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            super.exceptionCaught(ctx, e);
        }
    }
}
