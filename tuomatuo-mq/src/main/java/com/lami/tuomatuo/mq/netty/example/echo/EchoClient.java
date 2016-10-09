package com.lami.tuomatuo.mq.netty.example.echo;

import com.lami.tuomatuo.mq.netty.bootstrap.ClientBootstrap;
import com.lami.tuomatuo.mq.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by xujiankang on 2016/9/29.
 */
public class EchoClient {

    public static void main(String[] args) throws Exception {


        // Parse options.
        String host = "127.0.0.1";
        int port = 11027;
        int firstMessageSize = 256;

        // Start client.
        ChannelFactory factory =
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);
        EchoHandler handler = new EchoHandler(firstMessageSize);

        bootstrap.getPipeline().addLast("handler", handler);
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        bootstrap.connect(new InetSocketAddress(host, port));

        // Start performance monitor.
        new ThroughputMonitor(handler).start();
    }
}
