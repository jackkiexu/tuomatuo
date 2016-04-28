package com.lami.tuomatuo.search.base.redis;

import org.redisson.Config;
import org.redisson.Redisson;

/**
 * Created by xujiankang on 2016/4/27.
 */
public class RedissonClient {

    private static RedissonClient instance;

    private RedissonClient(String fileName) throws Exception{

    }

    public void init(String fileName) throws Exception{
    }

    public Redisson getSingleClient(String host){
        org.redisson.Config config = new Config();
        config.useSingleServer().setAddress(host)
                .setConnectionPoolSize(1000);
        Redisson redisson = Redisson.create(config);
        return redisson;
    }

    public Redisson getMasterSlaveClient(String master, String slave){
        Config config = new Config();
        return null;
    }
}
