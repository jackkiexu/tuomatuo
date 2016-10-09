package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 9/16/16.
 */
public class ValueListOutput<V> extends CommandOutput<List<V>> {

    private List<V> list = new ArrayList<V>();

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public ValueListOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public List<V> get() throws RedisException {
        errorCheck();
        return list;
    }

    public void set(ByteBuffer bytes){
        list.add(bytes == null ? null : (V)codec.decodeValue(bytes));
    }
}
