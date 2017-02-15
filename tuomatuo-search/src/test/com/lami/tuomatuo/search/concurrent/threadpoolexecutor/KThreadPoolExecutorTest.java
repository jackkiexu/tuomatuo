package com.lami.tuomatuo.search.concurrent.threadpoolexecutor;

import com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KThreadPoolExecutor;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2017/2/15.
 */
public class KThreadPoolExecutorTest {

    private static final Logger logger = Logger.getLogger(KThreadPoolExecutorTest.class);

    public static void main(String[] args) {
        KThreadPoolExecutor threadPoolExecutor = new KThreadPoolExecutor(8, 8, 8, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100000));
        logger.info(threadPoolExecutor.COUNT_BITS);
        logger.info("COUNT_BITS : " + Integer.toBinaryString(threadPoolExecutor.COUNT_BITS));
        logger.info("-1 : " + Integer.toBinaryString(-1));

        logger.info("CAPACITY : " +Integer.toBinaryString(threadPoolExecutor.CAPACITY));
        logger.info("RUNNING : " +Integer.toBinaryString(threadPoolExecutor.RUNNING));
        logger.info("SHUTDOWN : " +Integer.toBinaryString(threadPoolExecutor.SHUTDOWN));
        logger.info("STOP : " +Integer.toBinaryString(threadPoolExecutor.STOP));
        logger.info("TIDYING : " +Integer.toBinaryString(threadPoolExecutor.TIDYING));
        logger.info("TERMINATED : " +Integer.toBinaryString(threadPoolExecutor.TERMINATED));
    }

}
