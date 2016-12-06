package com.ailami.tuomatuo.cache;

import com.ailami.tuomatuo.executors.DirectExecutorService;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xjk on 2016/12/6.
 */
@RunWith(value = Parameterized.class)
public class CacheTest {

    private static final Logger logger = Logger.getLogger(CacheTest.class);

    private ExecutorService executorService;

    @BeforeClass
    public static void beforeClass(){

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        Object[][] data = new Object[][]{
                {new DirectExecutorService()},
                {new DirectExecutorService()}
        };
        return Arrays.asList(data);
    }

    public CacheTest(ExecutorService executorService){
        this.executorService = executorService;
    }

    @Test(timeout = 5000)
    public void testGet() throws Throwable{
        final AtomicBoolean fromCache = new AtomicBoolean();
        Cache<String, String> cache = new Cache<String, String>(key -> {
            fromCache.getAndSet(true);
            return key;
        }, executorService, 1);

        Assert.assertEquals("foo", cache.get("foo"));
    }





}
