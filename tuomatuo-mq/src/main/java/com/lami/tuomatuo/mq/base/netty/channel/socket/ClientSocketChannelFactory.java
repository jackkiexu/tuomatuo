package com.lami.tuomatuo.mq.base.netty.channel.socket;

import com.lami.tuomatuo.mq.base.netty.channel.ChannelFactory;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipeline;

/**
 * Created by xujiankang on 2016/9/27.
 */
public interface ClientSocketChannelFactory extends ChannelFactory {

    SocketChannel newChannel(ChannelPipeline pipeline);

}
