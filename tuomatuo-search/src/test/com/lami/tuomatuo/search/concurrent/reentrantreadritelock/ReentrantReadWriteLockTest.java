package com.lami.tuomatuo.search.concurrent.reentrantreadritelock;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock 死锁 demo
 * Created by xujiankang on 2017/2/6.
 */
public class ReentrantReadWriteLockTest {

    private static final Logger logger = Logger.getLogger(ReentrantReadWriteLockTest.class);

    public static void main(String[] args) throws Exception{

        final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        logger.info("readWriteLock.readLock().lock() begin");
        readWriteLock.readLock().lock();
        logger.info("readWriteLock.readLock().lock() over");


        new Thread(){
            @Override
            public void run() {
                for(int i = 0; i< 10; i++){
                    logger.info(" ");
                    logger.info("Thread readWriteLock.readLock().lock() begin i:"+i);
                    readWriteLock.readLock().lock(); // 获取过一次就能再次获取, 但是若其他没有获取的线程因为 syn queue里面 head.next 是获取write的线程, 则到 syn queue 里面进行等待
                    logger.info("Thread readWriteLock.readLock().lock() over i:" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();

        Thread.sleep(10 * 1000);

        logger.info("readWriteLock.writeLock().lock() begin");
        readWriteLock.writeLock().lock();
        logger.info("readWriteLock.writeLock().lock() over");


    }

}
