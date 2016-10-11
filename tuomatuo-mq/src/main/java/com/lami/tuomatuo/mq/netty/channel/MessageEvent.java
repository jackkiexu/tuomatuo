package com.lami.tuomatuo.mq.netty.channel;

import java.net.SocketAddress;

/**
 * Created by xjk on 2016/9/21.
 */
public interface MessageEvent extends ChannelEvent {

    Object getMessage();
    SocketAddress getRemoteAddress();

}
