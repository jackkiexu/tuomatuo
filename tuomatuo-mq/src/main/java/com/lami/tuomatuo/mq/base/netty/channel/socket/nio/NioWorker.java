package com.lami.tuomatuo.mq.base.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.base.netty.buffer.ChannelBuffer;
import com.lami.tuomatuo.mq.base.netty.buffer.ChannelBuffers;
import com.lami.tuomatuo.mq.base.netty.channel.Channel;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelException;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelFuture;
import com.lami.tuomatuo.mq.base.netty.channel.Channels;
import com.lami.tuomatuo.mq.base.netty.util.NamePreservingRunnable;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(NioWorker.class);

    private int bossId;
    private int id;
    private Executor executor;
    private AtomicBoolean started = new AtomicBoolean();
    volatile Thread thread;
    volatile Selector selector;
    final Object selectorGuard = new Object();

    public NioWorker(int bossId, int id, Executor executor) {
        this.bossId = bossId;
        this.id = id;
        this.executor = executor;
    }

    void register(NioSocketChannel channel, ChannelFuture future) {
        boolean firstChannel = started.compareAndSet(false, true);
        Selector selector;
        if (firstChannel) {
            try {
                this.selector = selector = Selector.open();
            } catch (IOException e) {
                throw new ChannelException(
                        "Failed to create a selector.", e);
            }
        } else {
            selector = this.selector;
            if (selector == null) {
                do {
                    Thread.yield();
                    selector = this.selector;
                } while (selector == null);
            }
        }

        if (firstChannel) {
            try {
                channel.socket.register(selector, SelectionKey.OP_READ, channel);
                if (future != null) {
                    future.setSuccess();
                }
            } catch (ClosedChannelException e) {
                future.setFailure(e);
                throw new ChannelException(
                        "Failed to register a socket to the selector.", e);
            }

            boolean server = !(channel instanceof NioClientSocketChannel);
            if (server) {
                Channels.fireChannelOpen(channel);
            }

            Channels.fireChannelBound(channel, channel.getLocalAddress());
            Channels.fireChannelConnected(channel, channel.getRemoteAddress());

            String threadName =
                    (server ? "New I/O server worker #"
                            : "New I/O client worker #") + bossId + '-' + id;

            executor.execute(new NamePreservingRunnable(this, threadName));
        } else {
            synchronized (selectorGuard) {
                selector.wakeup();
                try {
                    channel.socket.register(selector, SelectionKey.OP_READ, channel);
                    if (future != null) {
                        future.setSuccess();
                    }
                } catch (ClosedChannelException e) {
                    future.setFailure(e);
                    throw new ChannelException(
                            "Failed to register a socket to the selector.", e);
                }

                Channels.fireChannelOpen(channel);
                Channels.fireChannelBound(channel, channel.getLocalAddress());
                Channels.fireChannelConnected(channel, channel.getRemoteAddress());
            }
        }
    }

    public void run() {
        thread = Thread.currentThread();

        boolean shutdown = false;
        Selector selector = this.selector;
        for (;;) {
            synchronized (selectorGuard) {
                // This empty synchronization block prevents the selector
                // from acquiring its lock.
            }
            try {
                int selectedKeyCount = selector.select(500);
                if (selectedKeyCount > 0) {
                    processSelectedKeys(selector.selectedKeys());
                }

                // Exit the loop when there's nothing to handle.
                // The shutdown flag is used to delay the shutdown of this
                // loop to avoid excessive Selector creation when
                // connections are registered in a one-by-one manner instead of
                // concurrent manner.
                if (selector.keys().isEmpty()) {
                    if (shutdown) {
                        synchronized (selectorGuard) {
                            if (selector.keys().isEmpty()) {
                                try {
                                    selector.close();
                                } catch (IOException e) {
                                    logger.warn(
                                            "Failed to close a selector.", e);
                                } finally {
                                    this.selector = null;
                                }
                                started.set(false);
                                break;
                            } else {
                                shutdown = false;
                            }
                        }
                    } else {
                        // Give one more second.
                        shutdown = true;
                    }
                } else {
                    shutdown = false;
                }
            } catch (Throwable t) {
                logger.warn(
                        "Unexpected exception in the selector loop.", t);

                // Prevent possible consecutive immediate failures.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore.
                }
            }
        }
    }

    private static void processSelectedKeys(Set<SelectionKey> selectedKeys) {
        for (Iterator<SelectionKey> i = selectedKeys.iterator(); i.hasNext();) {
            SelectionKey k = i.next();
            i.remove();

            if (!k.isValid()) {
                close(k);
                continue;
            }

            if (k.isReadable()) {
                read(k);
            }

            if (!k.isValid()) {
                close(k);
                continue;
            }

            if (k.isWritable()) {
                write(k);
            }
        }
    }


    private static void read(SelectionKey k) {
        ReadableByteChannel ch = (ReadableByteChannel) k.channel();
        NioSocketChannel channel = (NioSocketChannel) k.attachment();

        ReceiveBufferSizePredictor predictor =
                channel.getConfig().getReceiveBufferSizePredictor();
        ByteBuffer buf = ByteBuffer.allocate(predictor.nextReceiveBufferSize());

        int ret = 0;
        int readBytes = 0;
        boolean failure = true;
        try {
            while ((ret = ch.read(buf)) > 0) {
                readBytes += ret;
                if (!buf.hasRemaining()) {
                    break;
                }
            }
            failure = false;
        } catch (Throwable t) {
            Channels.fireExceptionCaught(channel, t);
        }

        if (readBytes > 0) {
            // Update the predictor.
            predictor.previousReceiveBufferSize(readBytes);

            // Fire the event.
            ChannelBuffer buffer;
            if (readBytes == buf.capacity()) {
                buffer = ChannelBuffers.wrappedBuffer(buf.array());
            } else {
                buffer = ChannelBuffers.wrappedBuffer(buf.array(), 0, readBytes);
            }
            Channels.fireMessageReceived(channel, buffer);
        }

        if (ret < 0 || failure) {
            close(k);
        }
    }


    private static void write(SelectionKey k) {
        NioSocketChannel ch = (NioSocketChannel) k.attachment();
        write(ch);
    }

    private static void close(SelectionKey key){
        NioSocketChannel channel = (NioSocketChannel) key.attachment();
        write(channel);
    }

    private static void setOpWrite(NioSocketChannel channel, boolean opWrite){
        NioWorker worker = channel.getWorker();
        if(worker == null){
            IllegalStateException cause = new IllegalStateException("Channel not connected yet (null worker)");
            Channels.fireExceptionCaught(channel, cause);
            return;
        }

        Selector selector = worker.selector;
        SelectionKey key = channel.socket.keyFor(selector);
        if(!key.isValid()){
            close(key);
            return;
        }

        int interestOps;
        boolean changed = false;
        if(opWrite){
            if(Thread.currentThread() == worker.thread){
                interestOps = key.interestOps();
                if((interestOps & SelectionKey.OP_WRITE) == 0){
                    interestOps |= SelectionKey.OP_WRITE;
                    key.interestOps(interestOps);
                    changed = true;
                }
            }else{
                synchronized (worker.selectorGuard){
                    selector.wakeup();
                    interestOps = key.interestOps();
                    if((interestOps & SelectionKey.OP_WRITE) == 0){
                        interestOps |= SelectionKey.OP_WRITE;
                        key.interestOps(interestOps);
                        changed = true;
                    }
                }
            }
        }else{
            if(Thread.currentThread() == worker.thread){
                interestOps = key.interestOps();
                if((interestOps & SelectionKey.OP_WRITE) != 0){
                    interestOps &= ~SelectionKey.OP_WRITE;
                    key.interestOps(interestOps);
                    changed = true;
                }
            }else{
                synchronized (worker.selectorGuard){
                    selector.wakeup();
                    interestOps = key.interestOps();
                    if((interestOps & SelectionKey.OP_WRITE) != 0){
                        interestOps &= ~SelectionKey.OP_WRITE;
                        key.interestOps(interestOps);
                        changed = true;
                    }
                }
            }
        }

        if(changed){
            channel.setInterestOpsNow(interestOps);
            Channels.fireChannelInterestChanged(channel, interestOps);
        }
    }


    static void write(NioSocketChannel channel) {
        if(channel.writeBuffer.isEmpty() && channel.currentWriteEvent == null){
            return ;
        }

        boolean addOpWrite = false;
        boolean removeOpWrite = false;

        int maxWritenBytes;
        if(channel.getConfig().isReadWriteFair()){
            int previousReceiveBufferSize = channel.getConfig().getReceiveBufferSizePredictor().nextReceiveBufferSize();
            maxWritenBytes = previousReceiveBufferSize + previousReceiveBufferSize >>> 1;
        }else{
            maxWritenBytes = Integer.MAX_VALUE;
        }
        int writtenBytes = 0;

        synchronized (channel.writeBuffer){
            for(;;){
                if(channel.writeBuffer.isEmpty() && channel.currentWriteEvent == null){
                    removeOpWrite = true;
                    break;
                }

                ChannelBuffer a ;
                if(channel.currentWriteEvent == null){
                    channel.currentWriteEvent = channel.writeBuffer.poll();
                    a = (ChannelBuffer)channel.currentWriteEvent.getMessage();
                    channel.currentWriteIndex = a.readerIndex();
                }else{
                    a = (ChannelBuffer)channel.currentWriteEvent.getMessage();
                }

                int localWrittenBytes = 0;

                try {
                    for(int i = channel.getConfig().getWriteSpinCount(); i > 0; i--){
                        localWrittenBytes = a.getBytes(channel.currentWriteIndex, channel.socket, Math.min(maxWritenBytes - writtenBytes, a.writerIndex() - channel.currentWriteIndex));

                        if(localWrittenBytes != 0){
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    channel.currentWriteEvent.getFuture().setFailure(e);
                    Channels.fireExceptionCaught(channel, e);
                }

                writtenBytes += localWrittenBytes;
                channel.currentWriteIndex += localWrittenBytes;
                if(channel.currentWriteIndex == a.writerIndex()){
                    channel.currentWriteEvent.getFuture().setSuccess();
                    channel.currentWriteEvent = null;
                }else{
                    addOpWrite = true;
                    break;
                }
            }
        }

        if(addOpWrite){
            setOpWrite(channel, true);
        }else if(removeOpWrite){
            setOpWrite(channel, false);
        }

    }

    static void setInterestOps(NioSocketChannel channel, ChannelFuture future, int interestOps) {
        NioWorker worker = channel.getWorker();
        if(worker == null){
            IllegalStateException cause = new IllegalStateException("Channel not connected yet (null worker)");
            future.setFailure(cause);
            Channels.fireExceptionCaught(channel, cause);
            return;
        }

        Selector selector = worker.selector;
        SelectionKey key = channel.socket.keyFor(selector);
        if(key == null || selector == null){
            IllegalStateException cause = new IllegalStateException("Channel not connected yet (SelectionKey not found)");
            future.setFailure(cause);
            Channels.fireExceptionCaught(channel, cause);
        }

        boolean changed = false;

        try {
            if(Thread.currentThread() == worker.thread){
                if(key.interestOps() != interestOps){
                    key.interestOps(interestOps);
                    changed = true;
                }else{
                    synchronized (worker.selectorGuard){
                        selector.wakeup();
                        if(key.interestOps() != interestOps){
                            key.interestOps(interestOps);
                            changed = true;
                        }
                    }
                }
            }

            future.setSuccess();
            if(changed){
                channel.setInterestOpsNow(interestOps);
                Channels.fireChannelInterestChanged(channel, interestOps);
            }
        } catch (Exception e) {
            e.printStackTrace();
            future.setFailure(e);
            Channels.fireExceptionCaught(channel, e);
        }

    }

    static void close(NioSocketChannel channel, ChannelFuture future){
        NioWorker worker = channel.getWorker();
        if(worker != null){
            Selector selector = worker.selector;
            SelectionKey key = channel.socket.keyFor(selector);
            if(key != null){
                key.cancel();
            }
        }

        boolean connected = channel.isConnected();
        boolean bound = channel.isBound();

        try {
            channel.socket.close();
            future.setSuccess();
            if(channel.setClosed()){
                if(connected){
                    if(channel.getInterestOps() != Channel.OP_WRITE){
                        channel.setInterestOpsNow(Channel.OP_WRITE);
                        Channels.fireChannelInterestChanged(channel, Channel.OP_WRITE);
                    }
                    Channels.fireChannelDisconnected(channel);
                }
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

}
