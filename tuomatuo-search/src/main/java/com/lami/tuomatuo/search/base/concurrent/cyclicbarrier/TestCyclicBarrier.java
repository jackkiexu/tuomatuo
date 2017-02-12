package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by xjk on 2016/5/9.
 */
public class TestCyclicBarrier {

    private static final Logger logger = Logger.getLogger(TestCyclicBarrier.class);

    private static final int THREAD_NUM = 5;


    public static void main(String[] args) {
        CyclicBarrier cb = new CyclicBarrier(THREAD_NUM, new Runnable() {
            public void run() {
                logger.info("Inside Barrier");
            }
        });

        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < THREAD_NUM; i++){
            Thread thread = new Thread(new WorkerThread(cb));
            threads.add(thread);
            thread.start();
        }

        // wait until done
        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("All Thread done()");
    }



    public static class WorkerThread implements Runnable{

        CyclicBarrier barrier;

        public WorkerThread(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        public void run() {
            try {
                logger.info("Working's waiting");
                // 线程在这里等待, 直到所有线程都到达barrier
                barrier.await();
                logger.info("Thread ID:" + Thread.currentThread().getId() + " Working");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }
}
