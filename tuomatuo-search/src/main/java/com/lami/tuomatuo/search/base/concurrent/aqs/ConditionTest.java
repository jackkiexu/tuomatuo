package com.lami.tuomatuo.search.base.concurrent.aqs;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 此demo用于测试 condition
 * Created by xujiankang on 2017/2/8.
 */
public class ConditionTest {

    private static final Logger logger = Logger.getLogger(ConditionTest.class);

    static final Lock lock = new ReentrantLock();
    static final Condition condition = lock.newCondition();

    public static void main(String[] args) throws Exception{

        final Thread thread1 = new Thread("Thread 1 "){
            @Override
            public void run() {
                lock.lock();
                logger.info(Thread.currentThread().getName() + " 正在运行 .....");

                try {
                    Thread.sleep(2 * 1000);
                    logger.info(Thread.currentThread().getName() + " 停止运行, 等待一个 signal ");
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(Thread.currentThread().getName() + " 获取一个 signal, 继续执行 ");
                lock.unlock();
            }
        };

        thread1.start();

        Thread.sleep(1 * 1000);

        Thread thread2 = new Thread("Thread 2 "){
            @Override
            public void run() {
                lock.lock();
                logger.info(Thread.currentThread().getName() + " 正在运行.....");
                thread1.interrupt(); // 看看中断后会怎么样


                try {
                    Thread.sleep(2 * 1000);
                }catch (Exception e){

                }

                condition.signal(); // 发送唤醒信号 从 AQS 的 Condition Queue 里面转移 Node 到 Sync Queue
                logger.info(Thread.currentThread().getName() + " 发送一个 signal ");
                logger.info(Thread.currentThread().getName() + " 发送 signal 结束");
                lock.unlock();
            }
        };

        thread2.start();

    }


}
