package com.lami.tuomatuo.mq.jafka.message;

/**
 * Created by xjk on 2016/10/8.
 */
public class InvalidMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidMessageException() {
        super();
    }

    public InvalidMessageException(String message) {
        super(message);
    }
}
