package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.util.NamePreservingRunnable;
import com.lami.tuomatuo.mq.netty.channel.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioServerSocketPipelineSink extends AbstractChannelSink {

    private static final Logger logger = Logger.getLogger(NioServerSocketPipelineSink.class);

    private final NioWorker[] workers;
    private final int id = nextId.incrementAndGet();
    private static final AtomicInteger nextId = new AtomicInteger();
    private AtomicInteger workerIndex = new AtomicInteger();

    NioServerSocketPipelineSink(Executor workerExecutor, int workerCount) {
        workers = new NioWorker[workerCount];
        for (int i = 0; i < workers.length; i ++) {
            workers[i] = new NioWorker(id, i + 1, workerExecutor);
        }
    }

    public void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception {
        Channel channel = e.getChannel();
        if(channel instanceof NioServerSocketChannel){
            handleServerSocket(e);
        }else if(channel instanceof NioSocketChannel){
            handleAcceptedSocket(e);
        }
    }

    private void handleServerSocket(ChannelEvent e){
        if(!(e instanceof ChannelStateEvent)){
            return ;
        }

        ChannelStateEvent event = (ChannelStateEvent)e;
        NioServerSocketChannel channel = (NioServerSocketChannel)event.getChannel();
        ChannelFuture future = event.getFuture();
        ChannelState state = event.getState();
        Object value = event.getValue();

        switch(state){
            case OPEN:
                if(Boolean.FALSE.equals(value)){
                    close(channel, future);
                }
                break;
            case BOUND:
                if(value != null){
                    bind(channel, future, (SocketAddress)value);
                }else{
                    close(channel, future);
                }
                break;
        }
    }

    private void handleAcceptedSocket(ChannelEvent e){
        if(e instanceof ChannelStateEvent){
            ChannelStateEvent event = (ChannelStateEvent) e;
            NioSocketChannel channel = (NioSocketChannel)event.getChannel();
            ChannelFuture future = event.getFuture();
            ChannelState state = event.getState();
            Object value = event.getValue();

            switch (state){
                case OPEN:
                    if(Boolean.FALSE.equals(value)){
                        NioWorker.close(channel, future);
                    }
                    break;
                case BOUND:
                case CONNECTED:
                    if(value == null){
                        NioWorker.close(channel, future);
                    }
                    break;
                case INTEREST_OPS:
                    NioWorker.setInterestOps(channel, future, (Integer)value);
                    break;
            }
        }else if(e instanceof MessageEvent){
            MessageEvent event = (MessageEvent) e;
            NioSocketChannel channel = (NioSocketChannel)event.getChannel();
            channel.writeBuffer.offer(event);
            NioWorker.write(channel);
        }
    }

    private void bind (NioServerSocketChannel channel, ChannelFuture future, SocketAddress localAddress){
        boolean bound = false;
        boolean bossStarted = false;

        try {
            channel.socket.socket().bind(localAddress, channel.getConfig().getBacklog());
            bound = true;

            future.setSuccess();
            Channels.fireChannelBound(channel, channel.getLocalAddress());
            Executor bossExecutor = ((NioServerSocketChannelFactory)channel.getFactory()).bossExecutor;
            bossExecutor.execute(new NamePreservingRunnable(new Boss(channel), "New I/O server boss # " + id + "(channelId : " + channel.getId() + ", " + channel.getLocalAddress() +")"));
            bossStarted = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(!bossStarted && bound){
                close(channel, future);
            }
        }
    }

    private void close(NioServerSocketChannel channel, ChannelFuture future){
        boolean bound = channel.isBound();
        try {
            channel.socket.close();
            future.setSuccess();
            if(channel.setClosed()){
                if(bound){
                    Channels.fireChannelUnbound(channel);
                }
                Channels.fireChannelClosed(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            future.setFailure(e);
            Channels.fireExceptionCaught(channel, e);
        }
    }

    NioWorker nextWorker(){
        return workers[Math.abs(workerIndex.getAndIncrement() % workers.length)];
    }

    private class Boss implements Runnable{

        private NioServerSocketChannel channel;

        public Boss(NioServerSocketChannel channel) {
            this.channel = channel;
        }

        public void run() {
            for(;;){
                try {
                    java.nio.channels.SocketChannel acceptedSocket = channel.socket.accept();

                    try {
                        ChannelPipeline pipeline = channel.getConfig().getPipelineFactory().getPipeline();
                        NioWorker worker = nextWorker();
                        worker.register(new NioAcceptedSocketChannel(
                              channel.getFactory(), pipeline, channel, NioServerSocketPipelineSink.this, acceptedSocket, worker
                        ), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("Failed to initialize an accepted socket");
                        try {
                            acceptedSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            logger.info("Failed to close a partially accepted socket");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("Failed to accept a connection");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }
    }
}
