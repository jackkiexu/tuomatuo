package com.lami.tuomatuo.mq.nio.server;

import com.lami.tuomatuo.mq.nio.server.NioServer;

import java.nio.channels.SocketChannel;

/**
 * Created by xjk on 10/24/16.
 */
public class ServerDataEvent {

    public NioServer server;
    public SocketChannel socket;
    public byte[] data;

    public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
        this.server = server;
        this.socket = socket;
        this.data = data;
    }
}
