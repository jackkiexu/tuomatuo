package com.lami.tuomatuo.search.base.concurrent.unsafe.concurrency;

/**
 * Created by xjk on 2016/5/24.
 */
public class ThreadLocalExample {

    public static class MyRunnable implements Runnable {

        private ThreadLocal threadLocal = new ThreadLocal();

        public void run() {
            threadLocal.set((int) (Math.random() * 100D));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            System.out.println(threadLocal.get());
        }
    }

    public static void main(String[] args) {
        MyRunnable sharedRunnableInstance = new MyRunnable();
        Thread thread1 = new Thread(sharedRunnableInstance);

        Thread thread2 = new Thread(sharedRunnableInstance);
        thread1.start();
        thread2.start();
    }

}