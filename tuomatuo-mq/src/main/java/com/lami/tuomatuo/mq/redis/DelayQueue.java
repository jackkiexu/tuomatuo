package com.lami.tuomatuo.mq.redis;


import org.redisson.Config;
import org.redisson.Redisson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A Delay base on Redis
 * Created by xjk on 11/8/16.
 */
public class DelayQueue {

    private Jedis jedis;

    public DelayQueue() {
        //连接redis服务器，192.168.0.100:6379
        jedis = new Jedis("192.168.0.100", 6379);
    }

    public void queueMessage(String queue, String message, Integer delay, TimeUnit timeUnit){
        long time = System.currentTimeMillis()/1000 + timeUnit.toSeconds(delay);
        jedis.zadd(queue, time, message);
    }

    public Set<String> getMessages(String queue){
        long startTime = 0;
        long endTime = System.currentTimeMillis()/1000;
        Transaction tx = jedis.multi();
        Set<String> result = jedis.zrangeByScore(queue, startTime, endTime);
        //TODO check result
        jedis.zremrangeByScore(queue, startTime, endTime);
        tx.exec();
        return result;
    }
}
