package com.lami.tuomatuo.mq.redis;

import com.lami.tuomatuo.mq.lettuce.RedisAsyncConnection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * 2
 * Created by xujiankang on 2016/9/23.
 */
public class AsyncConnectionTest extends AbstractCommandTest {

    private static final Logger logger = Logger.getLogger(AsyncConnectionTest.class);

    private RedisAsyncConnection<String, String> async;

    @Before
    public void openAsyncConnection() throws Exception{
        async = redis.getAsyncConnection();
    }

    @Test
    public void booleanCommand() throws Exception{
        async.set(key, value);
        async.exists(key);
    }



}
