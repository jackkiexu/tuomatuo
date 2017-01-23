package com.lami.tuomatuo.search.base.concurrent.tree.lock;

import com.lami.tuomatuo.search.base.concurrent.lock.ClhSpinLock;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/1/23.
 */
public class ClhSpinLockTest {

    private static final Logger logger = Logger.getLogger(ClhSpinLockTest.class);


    public static void main(String[] args) throws Exception{
        test2();
    }

    private static void test2(){
        final ClhSpinLock lock = new ClhSpinLock();

        lock.lock();
        logger.info("lock1 success");
        lock.lock();
        logger.info("lock2 success");

        lock.unlock();
        logger.info("unlock1 success");
        lock.unlock();
        logger.info("unlock2 success");
    }

    private static void test1(){
        final ClhSpinLock lock = new ClhSpinLock();

        lock.lock();

        for(int i = 0; i < 10; i++){
            new Thread(){
                @Override
                public void run() {
                    lock.lock();
                    logger.info(Thread.currentThread().getName() + " acquire the lock");
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock.unlock();
                }
            }.start();
        }

        logger.info("main thread unlock");
        lock.unlock();
    }
}
