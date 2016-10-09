package com.lami.tuomatuo.mq.redis;

import com.lami.tuomatuo.mq.lettuce.RedisClient;
import com.lami.tuomatuo.mq.lettuce.RedisConnection;
import com.lami.tuomatuo.mq.lettuce.ScoredValue;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xujiankang on 2016/9/19.
 */
public abstract class AbstractCommandTest {

    protected  Logger logger = Logger.getLogger(getClass());

    public static final String host = "192.168.1.21";
    public static final int    port = 6379;

    public static final String authHost = host;
    public static final int    authPort = 6379;
    public static final String passwd = "";

    protected RedisClient client = new RedisClient(host, port);
    protected RedisConnection<String, String> redis;
    protected String key = "key";
    protected String value = "value";

    @Before
    public final void openConnection() throws Exception{
        redis = client.connect();
        redis.flushall();
    }

    @After
    public final void closeConnection() throws Exception{
        redis.close();
    }

    protected List<String> list(String...args){
        return Arrays.asList(args);
    }

    protected List<Object> list(Object...args){
        return Arrays.asList(args);
    }

    protected List<ScoredValue<String>> svlist(ScoredValue<String>... args){
        return Arrays.asList(args);
    }

    protected ScoredValue<String> sv(double score, String value){
        return new ScoredValue<String>(score, value);
    }

    protected Set<String> set(String... args){
        return new HashSet<String>(Arrays.asList(args));
    }

}
