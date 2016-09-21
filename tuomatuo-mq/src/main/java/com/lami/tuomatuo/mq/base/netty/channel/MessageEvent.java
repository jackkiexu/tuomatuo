package com.lami.tuomatuo.mq.base.netty.channel;

import java.net.SocketAddress;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface MessageEvent extends ChannelEvent {

    Object getMessage();
    SocketAddress getRemoteAddress();

}
