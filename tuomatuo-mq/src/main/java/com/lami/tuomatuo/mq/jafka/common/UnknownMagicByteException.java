package com.lami.tuomatuo.mq.jafka.common;

/**
 * Created by xjk on 9/25/16.
 */
public class UnknownMagicByteException extends RuntimeException {

    public UnknownMagicByteException() {
        super();
    }

    public UnknownMagicByteException(String message) {
        super(message);
    }
}
