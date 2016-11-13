package com.lami.tuomatuo.search.base.concurrent.spinlock;

import org.apache.log4j.Logger;

/**
 * Created by xjk on 11/12/16.
 */
public class MyReentrantLockTest {

    static  Logger logger = Logger.getLogger(MyReentrantLockTest.class);


    public static void main(String[] args) {

        final MyReentrantLock lock = new MyReentrantLock();

        new Thread(new Runnable() {
            public void run() {
                try {
                    logger.info("线程1 lock begin");
                    lock.lock();
                    logger.info("线程1 lock success");
                    Thread.sleep(3000);
                } catch (Exception e ){
                    e.printStackTrace();
                } finally {
                    logger.info("线程1 unlock begin");
                    lock.unlock();
                    logger.info("线程1 unlock success");
                }

            }
        }, "Thread1"){}.start();


        new Thread(new Runnable() {
            public void run() {
                try {
                    logger.info("线程2 lock begin");
                    lock.lock();
                    logger.info("线程2 lock success");
                    Thread.sleep(5000);

                    logger.info("线程2 unlock begin");
                    lock.unlock();
                    logger.info("线程2 unlock success");

                } catch (Exception e ){
                    logger.info(e.getMessage());
                    e.printStackTrace();
                } finally {

                }

            }
        }, "Thread2"){}.start();
    }

}
