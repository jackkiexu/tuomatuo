package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xjk on 2016/9/21.
 */
public interface ExceptionEvent extends ChannelEvent {
    Throwable getCause();
}
