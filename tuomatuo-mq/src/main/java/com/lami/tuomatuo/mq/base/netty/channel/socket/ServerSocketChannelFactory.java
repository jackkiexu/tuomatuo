package com.lami.tuomatuo.mq.base.netty.channel.socket;

import com.lami.tuomatuo.mq.base.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipeline;

/**
 * Created by xujiankang on 2016/9/28.
 */
public interface ServerSocketChannelFactory extends ChannelFactory {
    ServerSocketChannel newChannel(ChannelPipeline pipeline);
}
