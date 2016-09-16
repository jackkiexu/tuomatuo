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
public class StringListOutput extends CommandOutput<List<String>> {

    private List<String> list = new ArrayList<String>();

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public StringListOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public List<String> get() throws RedisException {
        errorCheck();
        return list;
    }

    public void set(ByteBuffer bytes){
        list.add(bytes == null ? null: decodeAscii(bytes));
    }
}
