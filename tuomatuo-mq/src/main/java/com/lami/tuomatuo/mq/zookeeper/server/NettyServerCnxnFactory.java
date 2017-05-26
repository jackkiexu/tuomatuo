package com.lami.tuomatuo.mq.zookeeper.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class NettyServerCnxnFactory extends ServerCnxnFactory {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerCnxnFactory.class);

    public ServerBootstrap bootstrap;
    public Channel parentChannel;
    public ChannelGroup allChannels = new DefaultChannelGroup("zkServerCnxns");
    public HashMap<InetAddress, Set<NettyServerCnxn>> ipMap = new HashMap<>();

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
