package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 9/16/16.
 */
public class KeyListOutput<K> extends CommandOutput<List<K>> {

    private List<K> keys = new ArrayList<K>();

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public KeyListOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public List<K> get() throws RedisException {
        errorCheck();
        return keys;
    }

    public void set(ByteBuffer bytes){
        keys.add((K)codec.decodeKey(bytes));
    }
}
