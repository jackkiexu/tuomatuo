package com.lami.tuomatuo.mq.nio2;

import java.nio.channels.SocketChannel;

/**
 * Created by xjk on 10/30/16.
 */
public class Socket {

    public SocketChannel socketChannel = null;

    public Socket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
