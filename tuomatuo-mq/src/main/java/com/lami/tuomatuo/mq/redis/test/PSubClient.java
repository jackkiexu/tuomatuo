package com.lami.tuomatuo.mq.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by xujiankang on 2016/9/12.
 */
public class PSubClient {

    private Jedis jedis;//
    private JedisPubSub listener;//单listener

    public PSubClient(String host,int port,String clientId){
        jedis = new Jedis(host,port);
        listener = new PPrintListener(clientId, new Jedis(host, port));
    }

    public void sub(String channel){
        jedis.subscribe(listener, channel);
    }

    public void unsubscribe(String channel){
        listener.unsubscribe(channel);
    }

}
