package com.lami.tuomatuo.search.base.concurrent.semaphore;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xjk on 9/15/16.
 */
public class SemaphoreExample implements Runnable {

    private static Logger logger = Logger.getLogger(SemaphoreExample.class);

    private static final Semaphore semaphore = new Semaphore(3, true); // 初始化 Semaphore, 限流阀值 为3, 并且指定为公平模式
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new SemaphoreExample()); // 执行 permit 的获取,
        }
        executorService.shutdown();
    }

    public void run() {
        while(counter.incrementAndGet() <= 10) { // Semaphore 被循环获取 5次
            try {
                semaphore.acquire();                // 进行 permit 的获取
                if(semaphore.availablePermits() == 0){
                    // permits 被获取光, 进行sleep
                    logger.info(" permits 被耗光 进行 sleep");
                    Thread.sleep(5 * 1000);
                    logger.info(" permits 被耗光 进行 sleep over");
                }
            } catch (InterruptedException e) {
                logger.info("["+Thread.currentThread().getName()+"] Interrupted in acquire().");
            }
            logger.info("["+Thread.currentThread().getName()+"] semaphore acquired: "+counter.get());
            semaphore.release();
        }
    }
}