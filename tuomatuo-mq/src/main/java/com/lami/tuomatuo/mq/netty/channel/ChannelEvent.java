package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelEvent {

    Channel getChannel();
    ChannelFuture getFuture();

}