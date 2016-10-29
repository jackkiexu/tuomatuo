package com.lami.tuomatuo.mq.nio.server;

import java.nio.channels.SocketChannel;

/**
 * Created by xjk on 10/24/16.
 */
public class ChangeRequest {

    public static final int REGISTER = 1;
    public static final int CHANGEOPS = 2;

    public SocketChannel socketChannel;
    public int type;
    public int ops;

    public ChangeRequest(SocketChannel socketChannel, int type, int ops) {
        this.socketChannel = socketChannel;
        this.type = type;
        this.ops = ops;
    }
}
