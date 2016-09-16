package com.lami.tuomatuo.mq.redis.lettuce;

/**
 * Created by xjk on 9/16/16.
 */
public class RedisException extends RuntimeException {

    public RedisException(String msg){super((msg));}

    public RedisException(String msg, Throwable e){
        super(msg, e);
    }

}
