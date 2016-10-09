package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;
import com.lami.tuomatuo.mq.lettuce.ScoredValue;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 9/16/16.
 */
public class ScoredValueListOutput<V> extends CommandOutput<List<ScoredValue<V>>> {

    private List<ScoredValue<V>> list = new ArrayList<ScoredValue<V>>();
    private V value;
    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public ScoredValueListOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public List<ScoredValue<V>> get() throws RedisException {
        errorCheck();
        return list;
    }

    public void set(ByteBuffer bytes){
        if(value == null){
            value = (V)codec.decodeValue(bytes);
            return;
        }
        double score = Double.parseDouble(decodeAscii(bytes));
        list.add(new ScoredValue<V>(score, value));
        value = null;
    }
}
