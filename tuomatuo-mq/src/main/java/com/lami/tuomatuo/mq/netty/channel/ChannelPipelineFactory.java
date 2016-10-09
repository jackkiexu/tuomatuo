package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelPipelineFactory {

    ChannelPipeline getPipeline() throws Exception;
}
