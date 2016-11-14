package com.lami.tuomatuo.search.base.concurrent.CyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by xjk on 2016/5/9.
 */
public class TestCyclicBarrier {

    private static final int THREAD_NUM = 5;


    public static void main(String[] args) {
        CyclicBarrier cb = new CyclicBarrier(THREAD_NUM, new Runnable() {
            public void run() {
                System.out.println("Inside Barrier");
            }
        });

        for(int i = 0; i < THREAD_NUM; i++){
            new Thread(new WorkerThread(cb)).start();
        }
    }



    public static class WorkerThread implements Runnable{

        CyclicBarrier barrier;

        public WorkerThread(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        public void run() {
            try {
                System.out.println("Working's waiting");
                // 线程在这里等待, 直到所有线程都到达barrier
                barrier.await();
                System.out.println("Thread ID:" + Thread.currentThread().getId() + " Working");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }
}
