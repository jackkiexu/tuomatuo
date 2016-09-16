package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public class IntegerOutput extends CommandOutput {

    private Long value;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public IntegerOutput(RedisCodec codec) {
        super(codec);
    }

    @Override
    public Object get() throws RedisException {
        errorCheck();
        return value;
    }

    public void set(ByteBuffer bytes){
        value = null;
    }
}
