package com.lami.tuomatuo.mq.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Math.max;

/**
 * Created by xjk on 12/18/16.
 */
public class DefaultPromiseTest {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromiseTest.class);

    private static int stackOverflowDepth;

    @BeforeClass
    public static void beforeClass(){
        try{
            findStackOverflowDepth();
            throw new IllegalStateException("Expected StackOverflowError but didn't get it?");
        }catch (Exception e){
            logger.info("StackOverflowError depth" + stackOverflowDepth);
        }
    }

    private static void findStackOverflowDepth(){
        ++stackOverflowDepth;
        if(stackOverflowDepth > 10) return;
        findStackOverflowDepth();
    }

    private static int stackOverflowTestDepth() {
        return max(stackOverflowDepth << 1, stackOverflowDepth);
    }

    @Test(expected = CancellationException.class)
    public void testCancellationExceptionIsThrownWhenBlockingGet() throws InterruptedException, ExecutionException{
        final Promise<Void> promise = new DefaultPromise<>(ImmediateEventExecutor.INSTANCE);
        promise.cancel(false);
        logger.info("promise.get() : " + promise.get());
    }

    @Test(expected = CancellationException.class)
    public void testCancellationExceptionIsThrownWhenBlockingGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException{

        final Promise<Void> promise = new DefaultPromise<>(ImmediateEventExecutor.INSTANCE);
        promise.cancel(false);
        logger.info("promise.get() : " + promise.get(2, TimeUnit.SECONDS));

    }

}
