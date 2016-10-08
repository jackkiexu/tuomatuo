package com.lami.tuomatuo.mq.jafka.common;

/**
 * Created by xujiankang on 2016/10/8.
 */
public class OffsetOutOfRangeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OffsetOutOfRangeException() {
        super();
    }

    public OffsetOutOfRangeException(String message) {
        super(message);
    }
}
