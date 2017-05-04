package com.lami.tuomatuo.mq.zookeeper.server;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class NettyServerCnxnFactory extends ServerCnxnFactory {
    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public Iterable<ServerCnxn> getConnections() {
        return null;
    }

    @Override
    public void closeSession(long sessionId) {

    }

    @Override
    public void configure(InetSocketAddress addr, int maxClientCnxns) throws IOException {

    }

    @Override
    public int getMaxClientCnxnsPerHost() {
        return 0;
    }

    @Override
    public void setMaxClientCnxnsPerHost(int max) {

    }

    @Override
    public void startup(ZooKeeperServer zkServer) throws IOException, InterruptedException {

    }

    @Override
    public void join() throws InterruptedException {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void start() {

    }

    @Override
    public void closeAll() {

    }

    @Override
    public InetSocketAddress getlocalAddress() {
        return null;
    }
}
