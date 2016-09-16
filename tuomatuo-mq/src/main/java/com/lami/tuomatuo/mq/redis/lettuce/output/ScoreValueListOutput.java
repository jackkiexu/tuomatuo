package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.ScoreValue;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 9/16/16.
 */
public class ScoreValueListOutput<V> extends CommandOutput<List<ScoreValue<V>>> {

    private List<ScoreValue<V>> list = new ArrayList<ScoreValue<V>>();
    private V value;
    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public ScoreValueListOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public List<ScoreValue<V>> get() throws RedisException {
        errorCheck();
        return list;
    }

    public void set(ByteBuffer bytes){
        if(value == null){
            value = (V)codec.decodeValue(bytes);
            return;
        }
        double score = Double.parseDouble(decodeAscii(bytes));
        list.add(new ScoreValue<V>(score, value));
        value = null;
    }
}
