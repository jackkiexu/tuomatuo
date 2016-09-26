package com.lami.tuomatuo.mq.base.netty.channel;

import lombok.Data;

/**
 * Created by xujiankang on 2016/9/26.
 */
@Data
public class DefaultExceptionEvent extends DefaultChannelEvent implements ExceptionEvent {

    private Throwable cause;

    public DefaultExceptionEvent(Channel channel, ChannelFuture future, Throwable cause) {
        super(channel, future);
        this.cause = cause;
    }

    public DefaultExceptionEvent(Channel channel, ChannelFuture future) {
        super(channel, future);
    }

    public Throwable getCause() {
        return null;
    }
}
