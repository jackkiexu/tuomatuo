package com.lami.tuomatuo.search.base.concurrent.condition;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujiankang on 2017/1/24.
 */
public class ConditionTest {

    private static final Logger logger = Logger.getLogger(ConditionTest.class);

    private static ReentrantLock reentrantLock = new ReentrantLock();
    private static Condition condition = reentrantLock.newCondition();


    public static void main(String[] args) {

        Thread thread1 = new Thread("WaitThread"){
            @Override
            public void run() {
                reentrantLock.lock();
                reentrantLock.lock();
                try {
                    logger.info("Thread " + Thread.currentThread().getName() + "需要等待一个信号");
                    condition.await();
                    logger.info("Thread " + Thread.currentThread().getName() + "拿到一个信号");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reentrantLock.unlock();
                reentrantLock.unlock();
            }
        };

        thread1.start();



        Thread thread2 = new Thread("signalThread"){
            @Override
            public void run() {
                reentrantLock.lock();
                logger.info("Thread " + Thread.currentThread().getName() + " 拿到了锁");
                try {

                    condition.signal();
                    logger.info("Thread " + Thread.currentThread().getName() + "释放了一个信号");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                reentrantLock.unlock();
            }
        };

        thread2.start();



    }

}
