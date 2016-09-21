package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface Channel {

    int OP_NONE = 0;
    int OP_READ = 1;
    int OP_WRITE = 4;
    int OP_READ_WRITE = OP_READ | OP_WRITE;


    ChannelFuture close();

}
