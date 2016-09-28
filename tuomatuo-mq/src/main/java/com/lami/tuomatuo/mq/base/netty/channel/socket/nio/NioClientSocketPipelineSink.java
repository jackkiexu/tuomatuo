package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.channel.*;
import com.lami.tuomatuo.mq.base.netty.util.NamePreservingRunnable;
import org.apache.log4j.Logger;

import java.io.IOException;
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


    public void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception {

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
                bossExecutor.execute(new NamePreservingRunnable("NEW I/O client boss #" + id, this));
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

                        /**
                         *
                         */

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
