package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public class DoubleOutput extends CommandOutput<Double> {

    private Double value;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public DoubleOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public Double get() throws RedisException {
        errorCheck();
        return value;
    }

    public void set(ByteBuffer bytes){
        value = (bytes == null) ? null: Double.parseDouble(decodeAscii(bytes));
    }
}
