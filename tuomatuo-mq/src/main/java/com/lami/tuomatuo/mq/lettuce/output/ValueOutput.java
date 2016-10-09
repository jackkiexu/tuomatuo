package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;
import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public class ValueOutput<V> extends CommandOutput<V> {

    private V value;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public ValueOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public V get() throws RedisException {
        errorCheck();
        return value;
    }

    public void set(ByteBuffer bytes){
        value = (bytes == null)? null:(V)codec.decodeValue(bytes);
    }
}
