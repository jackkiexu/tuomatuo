package com.lami.tuomatuo.mq.netty.channel.socket;

import com.lami.tuomatuo.mq.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.netty.channel.ChannelPipeline;

/**
 * Created by xjk on 2016/9/27.
 */
public interface ClientSocketChannelFactory extends ChannelFactory {

    ServerSocketChannel newChannel(ChannelPipeline pipeline);

}
