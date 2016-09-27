package com.lami.tuomatuo.mq.base.netty.channel.socket;

import com.lami.tuomatuo.mq.base.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by xujiankang on 2016/9/27.
 */
public interface SocketChannel extends Channel {

    SocketChannelConfig getConfig();
    InetSocketAddress getLocalAddress();
    InetSocketAddress getRemoteAddress();

}
