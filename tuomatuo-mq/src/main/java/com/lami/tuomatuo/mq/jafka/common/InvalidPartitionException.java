package com.lami.tuomatuo.mq.jafka.common;

/**
 * Created by xujiankang on 2016/10/8.
 */
public class InvalidPartitionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidPartitionException() {
        super();
    }

    public InvalidPartitionException(String message) {
        super(message);
    }
}
