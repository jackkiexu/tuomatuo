package com.lami.tuomatuo.search.base.concurrent.latch;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xujiankang on 2016/5/9.
 */
public class Teacher implements  Runnable{

    private CountDownLatch countDownLatch;

    public Teacher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        try {
            System.out.println("Teacher is waiting .....");

            countDownLatch.await();
            System.out.println("Teacher is collecting .....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
