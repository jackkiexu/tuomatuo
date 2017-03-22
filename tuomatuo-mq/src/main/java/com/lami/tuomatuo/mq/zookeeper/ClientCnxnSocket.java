package com.lami.tuomatuo.mq.zookeeper;

import java.net.SocketAddress;

/**
 * A ClientCnxnSocket does the lower level communication with a socket
 * implementation
 *
 * This code has been moved out of ClientCnxn so that a Netty implementation can be provided as an alternative to the NIO socket code
 *
 * Created by xujiankang on 2017/3/19.
 */
public abstract class ClientCnxnSocket {


    protected long sentCount = 0;
    protected long recvCount = 0;


    long getSent(){
        return sentCount;
    }

    long getRecvCount(){
        return recvCount;
    }

    long getSentCount(){
        return sentCount;
    }

    abstract SocketAddress getLocalSocketAddress();

    abstract SocketAddress getRemoteSocketAddress();
}
