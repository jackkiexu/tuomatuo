package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xjk on 2016/9/22.
 */
public interface ChildChannelStateEvent extends ChannelEvent {

    Channel getChildChannel();

}
