package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;

/**
 * Created by xjk on 9/16/16.
 */
public class BooleanOutput extends CommandOutput<Boolean> {

    private Boolean value;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public BooleanOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public Boolean get() throws RedisException {
        errorCheck();
        return value;
    }

    public void set(long integer){
        value = (integer == 1)? Boolean.TRUE:Boolean.FALSE;
    }
}
