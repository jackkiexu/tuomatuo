package com.lami.tuomatuo.search.base.concurrent.unsafe;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xjk on 2016/5/31.
 */
public class LockSupportAndWait {

    private static Logger logger = Logger.getLogger(LockSupportAndWait.class);

    public static void main(String[] args) throws Exception{
        test2();
    }


    private static void test2() throws Exception{
        Thread t1 = new Thread(){
            @Override
            public void run() {
                logger.info(Thread.currentThread().getName() + " begin park Thread");
                logger.info("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                LockSupport.park(this);
                logger.info("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                if(Thread.currentThread().isInterrupted()){
                    logger.info("Thread.currentThread().interrupt()" + Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                    logger.info("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                }
                logger.info(Thread.currentThread().getName() + " over park Thread");
            }
        };

        Thread t2 = new Thread(){
            @Override
            public void run() {
                logger.info(Thread.currentThread().getName() + " begin park Thread");
                logger.info("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                LockSupport.park(this);
                logger.info("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                if(Thread.currentThread().isInterrupted()){
                    logger.info("Thread.currentThread().interrupt()" + Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                }
                logger.info(Thread.currentThread().getName() + " over park Thread");
            }
        };

        t1.start();

        t2.start();

        Thread.sleep(2 * 1000);
        t1.interrupt();

        Thread.sleep(2 * 1000);
        LockSupport.unpark(t2);
    }



    private void test1()throws Exception{
        Thread t = new Thread(){
            public void run(){
                try {
                    logger.info("wait");

                    synchronized (this){
                        this.wait();
                    }
                    logger.info("notify work");
                    logger.info("==========================");
                    logger.info("park");

                    // 这里两次调用 park
                    LockSupport.park();
                    LockSupport.park();
                    logger.info("unpark work");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
        TimeUnit.MILLISECONDS.sleep(100);
        logger.info("go to unpark");
        LockSupport.unpark(t);

        logger.info("go to notify");
        goNotify(t);

        TimeUnit.MICROSECONDS.sleep(1000);

        logger.info("go to notify");
        goNotify(t);
        logger.info("go to notify");
        LockSupport.unpark(t);
    }



    public static void goNotify(Thread t){
        synchronized (t){
            t.notify();
        }
    }

}
