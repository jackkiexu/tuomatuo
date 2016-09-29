package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.*;
import com.lami.tuomatuo.mq.base.netty.util.NamePreservingRunnable;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioClientSocketPipelineSink extends AbstractChannelSink {

    private static final Logger logger = Logger.getLogger(NioClientSocketPipelineSink.class);

    private static final AtomicInteger nextId = new AtomicInteger();

    int id = nextId.incrementAndGet();
    Executor bossExecutor;
    Boss boss = new Boss();
    NioWorker[] workers;
    AtomicInteger workerIndex = new AtomicInteger();

    public NioClientSocketPipelineSink(Executor bossExecutor, Executor workerExecutor, int workerCount) {
        this.bossExecutor = bossExecutor;
        workers = new NioWorker[workerCount];
        for(int i = 0; i < workers.length; i++){
            workers[i] = new NioWorker(id, i+1, workerExecutor);
        }
    }

    public void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception {
        if(e instanceof ChannelStateEvent){
            ChannelStateEvent event = (ChannelStateEvent) e;
            NioClientSocketChannel channel = (NioClientSocketChannel)event.getChannel();
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
                    if(value != null){
                        bind(channel, future, (SocketAddress) value);
                    }else{
                        NioWorker.close(channel, future);
                    }
                    break;
                case CONNECTED:
                    if(value != null){
                        connect(channel, future, (SocketAddress)value);
                    }else{
                        NioWorker.close(channel, future);
                    }
                    break;
                case INTEREST_OPS:
                    NioWorker.setInterestOps(channel, future, (Integer)value);
                    break;
            }
        }else if(e instanceof MessageEvent){
            MessageEvent event = (MessageEvent)e;
            NioSocketChannel channel = (NioSocketChannel)event.getChannel();
            channel.writeBuffer.offer(event);
            NioWorker.write(channel);
        }
    }

    private void bind(NioClientSocketChannel channel, ChannelFuture future, SocketAddress localAddress){
        try {
            channel.socket.socket().bind(localAddress);
            channel.boundManually = true;
            future.setSuccess();
            Channels.fireChannelBound(channel, channel.getLocalAddress());
        } catch (IOException e) {
            future.setFailure(e);
            Channels.fireExceptionCaught(channel, e);
        }
    }

    private void connect(final NioClientSocketChannel channel, ChannelFuture future, SocketAddress remoteAddress){
        try {
            if(channel.socket.connect(remoteAddress)){
                NioWorker worker = nextWorker();
                channel.setWork(worker);
                worker.register(channel, future);
            }else{
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isCancelled()){
                            channel.close();
                        }
                    }
                });
                channel.connectFuture = future;
                boss.register(channel);
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

        private AtomicBoolean started = new AtomicBoolean();
        private volatile Selector selector;
        private final Object selectorGuard = new Object();

        Boss(){
            super();
        }

        void register(NioSocketChannel channel){
            boolean firstChannel = started.compareAndSet(false, true);
            Selector selector;
            if(firstChannel){
                try {
                    this.selector = selector = Selector.open();
                } catch (IOException e) {
                    throw new ChannelException("Failed to create a selector ");
                } finally {
                }
            }else{
                selector = this.selector;
                if(selector == null){
                    do{
                        Thread.yield();
                        selector = this.selector;
                    }while(selector == null);
                }
            }

            if(firstChannel){
                try {
                    channel.socket.register(selector, SelectionKey.OP_CONNECT, channel);
                } catch (ClosedChannelException e) {
                    throw new ChannelException("Failed to register a socket to the selector");
                }
                bossExecutor.execute(new NamePreservingRunnable( this, "NEW I/O client boss #" + id));
            }else{
                synchronized (selectorGuard){
                    selector.wakeup();
                    try {
                        channel.socket.register(selector, SelectionKey.OP_ACCEPT, channel);
                    } catch (ClosedChannelException e) {
                        throw new ChannelException("Failed to register a socket to the selector");
                    }
                }
            }
        }

        public void run() {
            boolean shutdown = false;
            Selector selector = this.selector;
            for(;;){
                synchronized (selectorGuard){
                    // This empty synchronization block prevents the selector
                    // from acquiring its lock

                    try {
                        int selectedKeyCount = selector.select(500);
                        if(selectedKeyCount > 0){
                            processSelectorKeys(selector.selectedKeys());
                        }

                        /** Exit the loop when there's nothing to handle
                         *  The shutdown flag is used to delay the shutdown of this loop to avoid
                         *  Selector creation when connection attempts are made in a one by one manner
                         *  instead of concurrent manner
                         */
                        if(selector.keys().isEmpty()){
                            if(shutdown){
                                synchronized (selectorGuard) {
                                    if (selector.keys().isEmpty()) {

                                        try {
                                            if (selector.keys().isEmpty()) {
                                                selector.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            this.selector = null;
                                        }
                                        started.set(false);
                                        break;

                                    } else {
                                        shutdown = false;
                                    }
                                }
                            }else{
                                // Give one more second
                                shutdown = false;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }

        }

        private void processSelectorKeys(Set<SelectionKey> selectedKeys){
            for(Iterator<SelectionKey> i = selectedKeys.iterator(); i.hasNext();){
                SelectionKey k = i.next();
                i.remove();
                if(!k.isValid()){
                    close(k);
                    continue;
                }

                if(k.isConnectable()){
                    connect(k);
                }
            }
        }

        private void connect(SelectionKey k){
            NioClientSocketChannel ch = (NioClientSocketChannel)k.attachment();
            try {
                if(ch.socket.finishConnect()){
                    k.cancel();
                    NioWorker worker = nextWorker();
                    ch.setWork(worker);
                    worker.register(ch, ch.connectFuture);
                }
            } catch (IOException e) {
                k.cancel();
                ch.connectFuture.setFailure(e);
                Channels.fireExceptionCaught(ch, e);
                close(k);
            }
        }

        private void close(SelectionKey k){
            NioSocketChannel ch = (NioSocketChannel)k.attachment();
            NioWorker.close(ch, ch.getSucceededFuture());
        }

    }
}
