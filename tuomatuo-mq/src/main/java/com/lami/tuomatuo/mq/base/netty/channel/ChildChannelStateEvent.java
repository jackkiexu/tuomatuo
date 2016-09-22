package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/22.
 */
public interface ChildChannelStateEvent extends ChannelEvent {

    Channel getChildChannel();

}
