package com.lami.tuomatuo.mq.netty.channel;

import lombok.Data;

/**
 * Created by xujiankang on 2016/9/21.
 */
@Data
public class DefaultChannelEvent implements ChannelEvent {

    private Channel channel;
    private ChannelFuture future;

    public DefaultChannelEvent(Channel channel, ChannelFuture future) {

        if(channel == null){
            throw new NullPointerException("channel is null");
        }
        if(future == null){
            throw new NullPointerException("future is null");
        }

        this.channel = channel;
        this.future = future;
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelFuture getFuture() {
        return future;
    }

}
