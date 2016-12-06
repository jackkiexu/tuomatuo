package com.lami.tuomatuo.core.cache;

import com.lami.tuomatuo.core.base.AbstractCallable;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by xjk on 2016/10/17.
 */
public abstract class RedisCall<K, V> extends AbstractCallable<K, V> {

    protected   Logger logger = Logger.getLogger(getClass());

    protected ShardedJedis redisRead = null;
    protected ShardedJedis redisWrite = null;

    @Autowired
    private ShardedJedisPool readPool;
    @Autowired
    private ShardedJedisPool writePool;



    @Override
    protected Object before() {
        if(redisRead == null){
            redisRead = readPool.getResource();
        }
        return redisRead;
    }

    @Override
    protected Object after() {
        if(redisWrite == null){
            redisWrite = writePool.getResource();
        }
        return redisWrite;
    }
}
