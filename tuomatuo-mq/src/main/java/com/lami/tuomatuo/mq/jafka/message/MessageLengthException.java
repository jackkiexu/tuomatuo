package com.lami.tuomatuo.mq.jafka.message;

/**
 * Created by xjk on 2016/10/9.
 */
public class MessageLengthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MessageLengthException(String message) {
        super(message);
    }

}
