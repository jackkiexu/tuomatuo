package com.lami.tuomatuo.search.base.concurrent.latch;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/5/9.
 */
public class Student implements Runnable{

    private int num;
    private CountDownLatch countDownLatch;

    public Student(int num, CountDownLatch countDownLatch) {
        this.num = num;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        int time = new Random().nextInt(10);
        doExam(time);
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Student :" + num + " finished!");
        countDownLatch.countDown();
    }

    private void doExam(int time){
        System.out.println("Student " + num + "is doing the exam, and time is " + time);
    }
}
