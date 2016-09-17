package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xjk on 9/17/16.
 */
public class MapOutput<K, V> extends CommandOutput<Map<K, V>> {
    private Map<K, V> map = new HashMap<K, V>();
    private K key;

    public MapOutput(RedisCodec<K, V> codec) {
        super(codec);
    }

    @Override
    public Map<K, V> get() {
        errorCheck();
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(ByteBuffer bytes) {
        if (key == null) {
            key = (K) codec.decodeKey(bytes);
            return;
        }

        V value = (bytes == null) ? null : (V) codec.decodeValue(bytes);
        map.put(key, value);
        key = null;
    }
}