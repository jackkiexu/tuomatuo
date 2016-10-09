package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class SuccessedChannelFuture extends CompleteChannelFuture{
    public SuccessedChannelFuture(Channel channel) {
        super(channel);
    }

    public Throwable getCause(){
        return null;
    }

    public boolean isSuccess(){
        return true;
    }
}
