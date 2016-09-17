package com.lami.tuomatuo.mq.redis.lettuce;

/**
 * Created by xjk on 9/16/16.
 */
public class RedisCommandInterruptedException extends RedisException {
    public RedisCommandInterruptedException(String msg) {
        super(msg);
    }
    public RedisCommandInterruptedException(Throwable e) {
        super("Command interrupted", e);
    }
}
