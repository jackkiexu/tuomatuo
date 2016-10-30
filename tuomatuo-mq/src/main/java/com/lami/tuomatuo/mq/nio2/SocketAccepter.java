package com.lami.tuomatuo.mq.nio2;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by xjk on 10/30/16.
 */
public class SocketAccepter implements Runnable {

    private static final Logger logger = Logger.getLogger(SocketAccepter.class);

    private int tcpPort = 0;
    private ServerSocketChannel serverSocketChannel = null;

    private Queue socketQueue = null;

    public SocketAccepter(int tcpPort, Queue socketQueue) {
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }

    public void run() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                logger.info("Socket accepted : " + socketChannel);

                // TODO check if the queue can even accept more sockets
                this.socketQueue.add(new Socket(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
