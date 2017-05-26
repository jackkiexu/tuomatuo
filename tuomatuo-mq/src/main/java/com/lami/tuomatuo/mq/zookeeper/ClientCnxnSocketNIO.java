package com.lami.tuomatuo.mq.zookeeper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ClientCnxnSocketNIO extends ClientCnxnSocket {
    @Override
    SocketAddress getLocalSocketAddress() {
        return null;
    }

    @Override
    void cleanup() {

    }

    @Override
    void close() {

    }

    @Override
    void wakeupCnxn() {

    }

    @Override
    void enableWrite() {

    }

    @Override
    void disableWrite() {

    }

    @Override
    void enableReadWriteOnly() {

    }

    @Override
    void doTransport(int waitTimeOut, List<ClientCnxn.Packet> pendingQueue, LinkedList<ClientCnxn.Packet> outgoingQueue, ClientCnxn cnxn) throws IOException, InterruptedException {

    }

    @Override
    void testableCloseSocket() throws IOException {

    }

    @Override
    void sendPacket(ClientCnxn.Packet p) throws IOException {

    }

    @Override
    boolean isConnected() {
        return false;
    }

    @Override
    void connect(InetSocketAddress addr) throws IOException {

    }

    @Override
    SocketAddress getRemoteSocketAddress() {
        return null;
    }
}
