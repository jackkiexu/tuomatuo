package com.lami.tuomatuo.mq.jafka.network;


import com.lami.tuomatuo.mq.jafka.utils.Closer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Thread that accepts and configures new connections. There is only need for one of three
 *
 * Created by xjk on 2016/9/30.
 */
public class Acceptor extends AbstractServerThread {

    private int port;
    private Processor[] processors;
    private int sendBufferSize;
    private int receiveBufferSize;


    public Acceptor(int port, Processor[] processors, int sendBufferSize, int receiveBufferSize) {
        super();
        this.port = port;
        this.processors = processors;
        this.sendBufferSize = sendBufferSize;
        this.receiveBufferSize = receiveBufferSize;
    }

    public void run() {
        ServerSocketChannel serverChannel = null;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));
            serverChannel.register(getSelector(), SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        logger.info("Awaiting connection on port " + port);
        startupComplete();

        int currentProcessor = 0;
        while(isRunning()){
            int ready = -1;
            try {
                ready = getSelector().select(500l);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(ready < 0){
                continue;
            }
            Iterator<SelectionKey> iter = getSelector().selectedKeys().iterator();
            while(iter.hasNext() && isRunning()){
                try {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if(key.isAcceptable()){
                        accept(key, processors[currentProcessor]);
                    }else{
                        throw new IllegalStateException("Unrecongnized key state for acceptor thread");
                    }
                    currentProcessor = (currentProcessor + 1) % processors.length;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        // run over
        logger.info("Closing server socket and selector");
        Closer.closeQuietly(serverChannel, logger);
        Closer.closeQuietly(getSelector(), logger);
        shutdownComplete();
    }

    private void accept(SelectionKey key, Processor processor) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        serverSocketChannel.socket().setReceiveBufferSize(receiveBufferSize);

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setTcpNoDelay(true);
        socketChannel.socket().setSendBufferSize(sendBufferSize);
        processor.accept(socketChannel);
    }
}
