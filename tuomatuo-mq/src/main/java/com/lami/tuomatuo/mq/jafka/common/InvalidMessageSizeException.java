package com.lami.tuomatuo.mq.jafka.common;

/**
 * Created by xujiankang on 2016/10/8.
 */
public class InvalidMessageSizeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidMessageSizeException() {
        super();
    }

    public InvalidMessageSizeException(String message) {
        super(message);
    }
}
