package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xjk on 2016/9/21.
 */
public class ChannelException extends RuntimeException {

    private static final long serialVersionUID = 2908618315971075004L;

    public ChannelException() {
        super();
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }
}
