package com.lami.tuomatuo.mq.netty.channel;

import lombok.Data;

import java.net.SocketAddress;

/**
 * Created by xjk on 2016/9/26.
 */
@Data
public class DefaultMessageEvent extends DefaultChannelEvent implements MessageEvent {

    private Object message;
    private SocketAddress remoteAddress;

    public DefaultMessageEvent(Channel channel, ChannelFuture future, Object message, SocketAddress remoteAddress) {
        super(channel, future);
        this.message = message;
        this.remoteAddress = remoteAddress;
    }

    public DefaultMessageEvent(Channel channel, ChannelFuture future) {
        super(channel, future);
    }

    public Object getMessage() {
        return null;
    }

    public SocketAddress getRemoteAddress() {
        return null;
    }
}
