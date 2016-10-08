package com.lami.tuomatuo.mq.jafka.common;

import com.lami.tuomatuo.mq.jafka.message.InvalidMessageException;

import java.nio.ByteBuffer;

/**
 * A bi-directional mapping between error codes and exceptions x
 *
 * Created by xujiankang on 2016/10/8.
 */
public enum ErrorMapping {
    UnknowCode(-1),
    NoError(0),
    OffsetOutOfRangeCode(1),
    InvalidMessageCode(2),
    WrongPartionCode(3),
    InvalidFetchSizeCode(4);

    public short code;

    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

    ErrorMapping(int code) {
        this.code = (short)code;
    }

    public static ErrorMapping valueOf(Exception e){
        Class<?> clazz = e.getClass();
        if(clazz == OffsetOutOfRangeException.class) return OffsetOutOfRangeCode;
        if(clazz == InvalidMessageException.class) return InvalidMessageCode;
        if(clazz == InvalidPartitionException.class) return WrongPartionCode;
        if(clazz == InvalidMessageSizeException.class) return InvalidFetchSizeCode;

        return UnknowCode;
    }

    public static ErrorMapping valueOf(short code){
        switch(code){
            case 0:
                return NoError;
            case 1:
                return OffsetOutOfRangeCode;
            case 2:
                return InvalidMessageCode;
            case 3:
                return WrongPartionCode;
            case 4:
                return InvalidFetchSizeCode;
            default:
                return UnknowCode;

        }
    }
}
