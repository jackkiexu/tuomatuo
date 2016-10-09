package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;

import java.util.Date;

/**
 * Created by xjk on 9/16/16.
 */
public class DateOutput extends CommandOutput<Date> {

    private Date value;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public DateOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public Date get() throws RedisException {
        errorCheck();
        return value;
    }

    public void set(long time){
        value = new Date(time * 1000);
    }
}
