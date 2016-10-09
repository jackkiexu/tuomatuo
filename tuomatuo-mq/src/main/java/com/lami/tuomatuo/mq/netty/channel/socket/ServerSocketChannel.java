package com.lami.tuomatuo.mq.netty.channel.socket;

import com.lami.tuomatuo.mq.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by xujiankang on 2016/9/27.
 */
public interface ServerSocketChannel extends Channel {

    ServerSocketChannelConfig getConfig();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

}
