package com.lami.tuomatuo.mq.zookeeper;

import java.net.SocketAddress;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ClientCnxnSocketNIO extends ClientCnxnSocket {
    @Override
    SocketAddress getLocalSocketAddress() {
        return null;
    }

    @Override
    SocketAddress getRemoteSocketAddress() {
        return null;
    }
}
