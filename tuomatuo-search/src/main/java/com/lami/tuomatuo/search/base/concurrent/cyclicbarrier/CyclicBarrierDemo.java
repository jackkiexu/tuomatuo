package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import org.apache.log4j.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by xujiankang on 2017/2/7.
 */
public class CyclicBarrierDemo {

    private static final Logger logger = Logger.getLogger(CyclicBarrierDemo.class);

    public static void main(String[] args) {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        for(int i = 0; i < 3; i++){
            new Task("Thread :" + i, cyclicBarrier).start();
        }

        try {
            Thread.sleep(2 * 1000);
            logger.info("Main Thread 休息一段时间..........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("************************************ 开始新的 CyclicBarrier ********************");
        for(int i = 0; i < 3; i++){
            new Task("Thread :" + i, cyclicBarrier).start();
        }
    }


    static class Task extends Thread {

        private CyclicBarrier barrier;

        public Task(String threadName, CyclicBarrier barrier) {
            super(threadName);
            this.barrier = barrier;
        }

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + " 初始化开始....");

            try {
                Thread.sleep(2 * 1000); // 模拟初始化
                logger.info(Thread.currentThread().getName() + " 初始化结束, 等待其他task初始化结束, 然后继续运行");
                barrier.await(); // 在所有线程均到达此 barrier 前, 等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            logger.info(Thread.currentThread().getName() + " 其他 task 初始化结束, 开始运行");
        }
    }
}
