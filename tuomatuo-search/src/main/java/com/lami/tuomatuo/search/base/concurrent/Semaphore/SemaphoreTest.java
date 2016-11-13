package com.lami.tuomatuo.search.base.concurrent.Semaphore;

import org.apache.log4j.Logger;

import java.util.concurrent.Semaphore;

/**
 * Created by xjk on 11/12/16.
 */
public class SemaphoreTest {

    static Logger logger = Logger.getLogger(SemaphoreTest.class);

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(10);

        try {
            logger.info("semaphore.acquire 1");
            semaphore.acquire();
            logger.info("semaphore.acquire 2");
            semaphore.acquire();
            logger.info("semaphore.acquire 3");
            semaphore.acquire();
            logger.info("semaphore.acquire 4");
            semaphore.acquire();
            logger.info("semaphore.acquire 5");

            semaphore.release();
            semaphore.release();
            semaphore.release();
            semaphore.release();
            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }

}
