package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;
import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xjk on 9/16/16.
 */
public class MappOutput<K, V> extends CommandOutput<Map<K, V>> {

    private Map<K, V> map = new HashMap<K, V>();
    private K key;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public MappOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public Map<K, V> get() throws RedisException {
        errorCheck();
        return map;
    }

    public void set(ByteBuffer bytes){
        if(key == null){
            key = (K)codec.decodeKey(bytes);
            return ;
        }

        V value = (bytes == null) ? null : (V)codec.decodeKey(bytes);
        map.put(key, value);
        key = null;
    }
}
