package com.lami.tuomatuo.mq.lettuce.pubsub;

/**
 * Convenience adapter with an empty implementation of all
 * {@link RedisPubSubListener} callback methods
 * Created by xjk on 9/16/16.
 */
public class RedisPubSubAdapter<V> implements RedisPubSubListener<V> {

    public void message(String channel, V message) {

    }

    public void message(String pattern, String channel, V message) {

    }

    public void subscribed(String channel, long count) {

    }

    public void unsubscribed(String channel, long count) {

    }

    public void psubscribed(String pattern, long count) {

    }

    public void punsubscribed(String pattern, long count) {

    }
}
