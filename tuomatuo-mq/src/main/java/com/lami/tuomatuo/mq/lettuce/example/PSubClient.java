package com.lami.tuomatuo.mq.lettuce.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;

/**
 * Created by xjk on 2016/9/12.
 */
public class PSubClient {

    private Jedis jedis;//
    private JedisPubSub listener;//单listener

    public PSubClient(String host,int port,String clientId){
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
        jedisShardInfo.setPassword("redis12345");
        jedis = new Jedis(jedisShardInfo);
        listener = new PPrintListener(clientId, new Jedis(jedisShardInfo));
    }

    public void sub(String channel){
        jedis.subscribe(listener, channel);
    }

    public void unsubscribe(String channel){
        listener.unsubscribe(channel);
    }

}