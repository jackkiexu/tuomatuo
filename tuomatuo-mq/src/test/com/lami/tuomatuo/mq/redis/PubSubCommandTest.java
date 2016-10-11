package com.lami.tuomatuo.mq.redis;

import com.lami.tuomatuo.mq.lettuce.RedisClient;
import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.pubsub.RedisPubSubAdapter;
import com.lami.tuomatuo.mq.lettuce.pubsub.RedisPubSubConnection;
import com.lami.tuomatuo.mq.lettuce.pubsub.RedisPubSubListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static junit.framework.Assert.assertEquals;

/**
 * Created by xjk on 2016/9/19.
 */
public class PubSubCommandTest extends AbstractCommandTest implements RedisPubSubListener<String> {

    private RedisPubSubConnection<String, String> pubsub;

    private BlockingQueue<String> channels;
    private BlockingQueue<String> patterns;
    private BlockingQueue<String> messages;
    private BlockingQueue<Long> counts;

    private String channel = "channel0";
    private String pattern = "channel*";
    private String message = "msg!";

    @Before
    public void openPubSubConnection() throws Exception{
        pubsub = client.connectPubSub();
        pubsub.addListener(this);
        channels = new LinkedBlockingDeque<String>();
        patterns = new LinkedBlockingDeque<String>();
        messages = new LinkedBlockingDeque<String>();
        counts = new LinkedBlockingDeque<Long>();
        logger.info("openPubSubConnection complete");
    }

    @After
    public void closePubSubConnection() throws Exception{
        logger.info("closePubSubConnection begin");
        pubsub.close();
        logger.info("closePubSubConnection complete");
    }

    @Test
    public void auth() throws Exception{
        RedisClient client = new RedisClient(authHost, authPort);
        RedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(this);
        connection.subscribe(channel);
        assertEquals(channel, channels.take());
        connection.close();
    }

    @Test(expected = RedisException.class)
    public void close() throws Exception{
        redis.set("name", "nomoney");
        String v = redis.get("name");
        logger.info("********************************************************************V:" + v);
        redis.close();
    }

    @Test(timeout = 5000)
    public void message() throws Exception{
        pubsub.subscribe(channel);
        redis.publish(channel, message);

    }

    @Test(timeout = 100)
    public void adapter() throws Exception{
        final BlockingQueue<String> localSubscriptions = new LinkedBlockingQueue<String>();
        RedisPubSubAdapter<String> adapter = new RedisPubSubAdapter<String>(){
            @Override
            public void subscribed(String channel, long count) {
                super.subscribed(channel, count);
                localSubscriptions.add(channel);
            }
        };
        pubsub.addListener(adapter);
        pubsub.subscribe(channel);
        assertEquals(channel, localSubscriptions.take());
    }

    // RedisPubSubListener implementation

    public void message(String channel, String message) {
        channels.add(channel);
        messages.add(message);
    }

    public void message(String pattern, String channel, String message) {
        patterns.add(pattern);
        channels.add(channel);
        messages.add(message);
    }

    public void subscribed(String channel, long count) {
        channels.add(channel);
        counts.add(count);
    }

    public void unsubscribed(String channel, long count) {
        channels.add(channel);
        counts.add(count);
    }

    public void psubscribed(String pattern, long count) {
        patterns.add(pattern);
        counts.add(count);
    }

    public void punsubscribed(String pattern, long count) {
        patterns.add(pattern);
        counts.add(count);
    }
}
