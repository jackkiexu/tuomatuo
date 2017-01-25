package com.lami.tuomatuo.search.base.concurrent.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujiankang on 2017/1/25.
 */
public class KReentrantLockTest {

    public static void main(String[] args) throws Exception{
        final ReentrantLock reentrantLock = new ReentrantLock();

       /* reentrantLock.lock();
        Thread.sleep(2 * 1000);
        reentrantLock.unlock();*/


        for(int i = 0; i < 5; i++){
            new Thread("thread " + i + " " ){
                @Override
                public void run() {
                    try {
                        reentrantLock.lock();
                        Thread.sleep(2 * 1000);
                        reentrantLock.unlock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }


    }

}
