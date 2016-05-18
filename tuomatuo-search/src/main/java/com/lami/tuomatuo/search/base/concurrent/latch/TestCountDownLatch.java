package com.lami.tuomatuo.search.base.concurrent.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/5/9.
 */
public class TestCountDownLatch {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try {

            CountDownLatch countDownLatch = new CountDownLatch(3);

            Student student1 = new Student(101, countDownLatch);
            Student student2 = new Student(102, countDownLatch);
            Student student3 = new Student(103, countDownLatch);
            Teacher teacher = new Teacher(countDownLatch);

            executorService.execute(student1);
            executorService.execute(student2);
            executorService.execute(student3);
            executorService.execute(teacher);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                executorService.awaitTermination(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
