package com.lami.tuomatuo.mq.zookeeper;

import java.net.SocketAddress;

/**
 * ClientCnxnSocketNetty implements ClientCnxnSocket abstract methods
 * It's responsible for connecting to server. reading/writing network traffic and
 * being a layer between data and higher level packets
 * Created by xujiankang on 2017/3/19.
 */
public class ClientCnxnSocketNetty extends ClientCnxnSocket{
    @Override
    SocketAddress getLocalSocketAddress() {
        return null;
    }

    @Override
    SocketAddress getRemoteSocketAddress() {
        return null;
    }
}
