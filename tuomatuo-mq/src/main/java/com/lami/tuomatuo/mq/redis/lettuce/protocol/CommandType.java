package com.lami.tuomatuo.mq.redis.lettuce.protocol;

/**
 * Created by xjk on 9/16/16.
 */
public enum CommandType {

    // Connection
    AUTH, ECHO, PING, QUIT, SELECT;

    // Server

    public byte[] bytes;

    CommandType() {
        bytes = name().getBytes();
    }
}
