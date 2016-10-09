package com.lami.tuomatuo.mq.netty.channel.socket.nio;

import com.lami.tuomatuo.mq.netty.channel.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NioClientSocketChannel extends NioSocketChannel{

    private static Logger logger = Logger.getLogger(NioClientSocketChannel.class);

    public NioClientSocketChannel(Channel parent, ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink, SocketChannel socket) {
        super(parent, factory, pipeline, sink, socket);
    }
    NioClientSocketChannel(
            ChannelFactory factory, ChannelPipeline pipeline, ChannelSink sink) {

        super(null, factory, pipeline, sink, newSocket());
        Channels.fireChannelOpen(this);
    }

    private static SocketChannel newSocket(){
        SocketChannel socket;
        try {
            socket = SocketChannel.open();
        } catch (IOException e) {
           throw new ChannelException("Failed to open a socket", e);
        }
        boolean success =false;

        try {
            socket.configureBlocking(false);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!success){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return socket;
    }

    volatile NioWorker worker;
    volatile ChannelFuture connectFuture;
    volatile boolean boundManually;



    @Override
    NioWorker getWorker() {
        return worker;
    }

    @Override
    void setWork(NioWorker worker) {
        if(this.worker == null){
            this.worker = worker;
        }else if(this.worker != worker){
            // worker never change
            throw new IllegalStateException("Should not reach here");
        }
    }
}
