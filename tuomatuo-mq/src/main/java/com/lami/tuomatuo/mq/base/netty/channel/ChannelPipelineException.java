package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/22.
 */
public class ChannelPipelineException extends ChannelException {

    private static final long serialVersionUID = 337917421049885980L;

    public ChannelPipelineException() {
        super();
    }

    public ChannelPipelineException(Throwable cause) {
        super(cause);
    }

    public ChannelPipelineException(String message) {

        super(message);
    }

    public ChannelPipelineException(String message, Throwable cause) {


        super(message, cause);
    }
}
