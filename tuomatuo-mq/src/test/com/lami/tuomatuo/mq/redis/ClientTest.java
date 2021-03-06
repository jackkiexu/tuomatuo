package com.lami.tuomatuo.mq.redis;

import com.lami.tuomatuo.mq.lettuce.RedisException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by xjk on 2016/9/19.
 */
public class ClientTest extends AbstractCommandTest{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = RedisException.class)
    public void close() throws Exception{
        redis.set("name", "nomoney");
        String v = redis.get("name");
        logger.info("********************************************************************V:"+v);
        redis.close();
    }
}
