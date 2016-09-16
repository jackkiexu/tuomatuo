package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xjk on 9/16/16.
 */
public class ValueSetOutput<V> extends CommandOutput<Set<V>> {

    private Set<V> set = new HashSet<V>();

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public ValueSetOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public Set<V> get() throws RedisException {
        errorCheck();
        return set;
    }

    public void set(ByteBuffer bytes){
        set.add(bytes == null? null : (V)codec.decodeKey(bytes));
    }
}
