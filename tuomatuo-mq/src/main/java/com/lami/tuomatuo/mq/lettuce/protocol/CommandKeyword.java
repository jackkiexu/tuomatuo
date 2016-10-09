package com.lami.tuomatuo.mq.lettuce.protocol;

/**
 * Created by xjk on 9/16/16.
 */
public enum CommandKeyword {
    AFTER, AGGREGATE, ALPHA, ASC, BEFORE, BY, DESC, LIMIT, MAX, MIN,
    NO, OBJECT, ONE, RESETSTAT, STORE, SUM, WEIGHTS, WITHSCORES;

    public byte[] bytes;

    CommandKeyword() {
        bytes = name().getBytes();
    }
}
