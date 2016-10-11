package com.lami.tuomatuo.mq.jafka;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import org.apache.log4j.Logger;

/**
 * Created by xjk on 2016/9/14.
 */
public class AsynchronousRedis {

    private static final Logger logger = Logger.getLogger(AsynchronousRedis.class);

    public static void main(String[] args) {

        RedisClient redisClient = new RedisClient(RedisURI.create("redis://192.168.1.21:6379/0"));
        RedisConnectionPool<RedisConnection<String, String>>  connection = redisClient.pool(10, 1000);

        RedisConnection redisConnection = connection.allocateConnection();
        redisConnection.set("key", "no money");
        connection.freeConnection(redisConnection);
        connection.close();

        redisClient.shutdown();
    }
}
