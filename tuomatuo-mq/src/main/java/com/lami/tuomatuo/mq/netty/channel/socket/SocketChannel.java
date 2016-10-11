package com.lami.tuomatuo.mq.netty.channel.socket;

import com.lami.tuomatuo.mq.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by xjk on 2016/9/27.
 */
public interface SocketChannel extends Channel {

    SocketChannelConfig getConfig();
    InetSocketAddress getLocalAddress();
    InetSocketAddress getRemoteAddress();

}
