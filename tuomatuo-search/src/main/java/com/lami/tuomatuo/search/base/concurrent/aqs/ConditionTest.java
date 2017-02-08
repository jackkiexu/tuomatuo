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

        /**
         * 1. 线程 1 开始执行, 获取 lock, 然后开始睡眠 2秒
         * 2. 当线程1睡眠到 1秒时, 线程2开始执行, 但是lock被线程1获取, 所以 等待
         * 3. 线程 1 睡足2秒 调用 condition.await() 进行锁的释放, 并且将 线程1封装成一个 node 放到 condition 的 Condition Queue里面, 等待其他获取锁的线程给他 signal, 或对其进行中断(中断后可以到 Sync Queue里面进而获取 锁)
         * 4. 线程 2 获取锁成功, 中断 线程1, 线程被中断后,  node 从 Condition Queue 转移到 Sync Queue 里面, 但是 lock 还是被 线程2获取者, 所以 node呆在 Sync Queue 里面等待获取 lock
         * 5. 线程 2睡了 2秒, 开始 用signal唤醒 Condition Queue 里面的节点(此时代表 线程1的node已经到 Sync Queue 里面)
         * 6. 线程 2释放lock, 并且在 Sync Queue 里面进行唤醒等待获取锁的节点 node
         * 7. 线程1 得到唤醒, 获取锁
         * 8. 线程1 释放锁
         */

        final Thread thread1 = new Thread("Thread 1 "){
            @Override
            public void run() {
                lock.lock(); // 线程 1获取 lock
                logger.info(Thread.currentThread().getName() + " 正在运行 .....");

                try {
                    Thread.sleep(2 * 1000);
                    logger.info(Thread.currentThread().getName() + " 停止运行, 等待一个 signal ");
                    condition.await(); // 调用 condition.await 进行释放锁, 将当前节点封装成一个 Node 放入 Condition Queue 里面, 等待唤醒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info(Thread.currentThread().getName() + " 获取一个 signal, 继续执行 ");
                lock.unlock(); // 释放锁
            }
        };

        thread1.start();  // 线程 1 线运行

        Thread.sleep(1 * 1000);

        Thread thread2 = new Thread("Thread 2 "){
            @Override
            public void run() {
                lock.lock();        // 线程 2获取lock
                logger.info(Thread.currentThread().getName() + " 正在运行.....");
                thread1.interrupt(); // 对线程1 进行中断 看看中断后会怎么样? 结果 线程 1还是获取lock, 并且最后还进行 lock.unlock()操作

                try {
                    Thread.sleep(2 * 1000);
                }catch (Exception e){

                }
                condition.signal(); // 发送唤醒信号 从 AQS 的 Condition Queue 里面转移 Node 到 Sync Queue
                logger.info(Thread.currentThread().getName() + " 发送一个 signal ");
                logger.info(Thread.currentThread().getName() + " 发送 signal 结束");
                lock.unlock(); // 线程 2 释放锁
            }
        };

        thread2.start();

    }


}
