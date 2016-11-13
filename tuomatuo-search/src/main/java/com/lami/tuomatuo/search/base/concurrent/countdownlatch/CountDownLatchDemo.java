package com.lami.tuomatuo.search.base.concurrent.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xjk on 11/12/16.
 */
public class CountDownLatchDemo {

    private static int NUM = 10;
    private static CountDownLatch doneSignal = new CountDownLatch(NUM);
    private static CountDownLatch startSignal = new CountDownLatch(1);


    public static void main(String[] args) {
        for(int i = 0; i < NUM; i++){
            new Thread(){

                @Override
                public void run() {
                    try {
                        startSignal.await();
                        System.out.println(Thread.currentThread().getName() + " is running ...");
                        doneSignal.countDown();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }


        System.out.println("main为所有的线程的运行做准备。。。。");
        startSignal.countDown(); // 运行到这里上面的线程全部激活

        System.out.println("main 线程 awaiting ... ");
        try {
            doneSignal.await(); //main 线程在这里等待, 等待上面的所有线程全部执行后
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("main 线程又开始运行");

    }

}
