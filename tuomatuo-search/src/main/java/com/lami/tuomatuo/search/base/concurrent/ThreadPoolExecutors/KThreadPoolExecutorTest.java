package com.lami.tuomatuo.search.base.concurrent.ThreadPoolExecutors;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by xujiankang on 2016/12/30.
 */
public class KThreadPoolExecutorTest {

    private static final Logger logger = Logger.getLogger(KThreadPoolExecutorTest.class);
    static int N = 1000000;

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    public static void main(String[] args) throws Exception{
        for(int i = 0; i < 10; i++){
            int length = (i == 0) ? 1 : i * 5;
            logger.info(length + "\t");
            logger.info(doTest(new LinkedBlockingQueue<Integer>(length), N) + "\t");
            logger.info(doTest(new ArrayBlockingQueue<Integer>(length), N) + "\t");
            logger.info(doTest(new SynchronousQueue<Integer>(), N));
            logger.info("");
        }
        executorService.shutdown();
    }

    private static long doTest(final BlockingQueue<Integer> q, final int n) throws Exception{
        long t = System.nanoTime();
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for(int i = 0; i < n; i++){
                    try{
                        q.put(i);
                    }catch (Exception e){

                    }
                }
                return null;
            }
        });

        Long result = executorService.submit(new Callable<Long>() {
            public Long call() throws Exception {
                long sum = 0;
                for (int i = 0; i < n; i++) {
                    try {
                        sum += q.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return sum;
            }
        }).get();

        t = System.nanoTime() - t;

        return (long)(1000000000.0  * N / t);
    }
}
