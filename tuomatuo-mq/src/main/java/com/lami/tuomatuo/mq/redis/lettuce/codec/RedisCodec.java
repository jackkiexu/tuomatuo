package com.lami.tuomatuo.mq.redis.lettuce.codec;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public abstract class RedisCodec<K, V> {

    /**
     * Decode the key output by redis
     *
     * @param bytes
     * @return
     */
    public abstract K decodeKey(ByteBuffer bytes);

    /**
     * Decode the value output by redis
     * @param bytes
     * @return
     */
    public abstract V decodeValue(ByteBuffer bytes);

    /**
     * Encode the key for output to redis
     * @param key
     * @return
     */
    public abstract byte[] encodeKey(K key);

    /**
     * Encode the value for output to redis
     * @param value
     * @return
     */
    public abstract byte[] encodeValue(V value);

}
