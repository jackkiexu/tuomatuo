package com.lami.tuomatuo.mq.redis.lettuce.pubsub;

/**
 * Interface for redis pub/sub listeners
 * Created by xjk on 9/16/16.
 */
public interface RedisPubSubListener<V> {

    /**
     * Message received from a channel subscription
     * @param channel
     * @param message
     */
    void message(String channel, V message);

    /**
     * Message received from a pattern subscription
     * @param pattern
     * @param channel
     * @param message
     */
    void message(String pattern, String channel, V message);

    /**
     * Subscribed to a channel
     * @param channel
     * @param count
     */
    void subscribed(String channel, long count);

    /**
     * unsubscribed from a channel
     * @param channel
     * @param count
     */
    void unsubscribed(String channel, long count);

    /**
     * Unsubscribed from a channel
     * @param pattern
     * @param count
     */
    void psubscribed(String pattern, long count);

    /**
     * Unsubscribed from a pattern
     * @param pattern
     * @param count
     */
    void punsubscribed(String pattern, long count);
}
