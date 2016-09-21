package com.lami.tuomatuo.mq.base.netty.channel;

import io.netty.channel.ChannelPipeline;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelFactory {
    Channel newChannel(ChannelPipeline pipline);
}
