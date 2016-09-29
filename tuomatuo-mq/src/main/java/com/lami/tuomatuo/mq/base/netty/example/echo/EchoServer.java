package com.lami.tuomatuo.mq.base.netty.example.echo;

import com.lami.tuomatuo.mq.base.netty.bootstrap.ServerBootstrap;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.base.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by xujiankang on 2016/9/29.
 */
public class EchoServer {

    private static final Logger logger = Logger.getLogger(EchoServer.class);

    public static void main(String[] args) {
        // Start server
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        EchoHandler handler = new EchoHandler(20);
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.getPipeline().addLast("handler", handler);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(11027));

        // Start performance monitor
        new ThroughputMonitor(handler).start();
    }

}
