package com.lami.tuomatuo.search.base.concurrent.threadlocal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2017/1/12.
 */
public class KThreadLocalTest {

    private static AtomicInteger ai = new AtomicInteger(0);
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return ai.getAndIncrement();
        }
    };

    public static void main(String[] args) {
        int threadNum = 5;
        Thread[] threads = new Thread[threadNum];
        for(int i=0;i<threadNum;i++){
            threads[i] = new Thread(){

                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+":"+threadLocal.get());
                }

            };
        }
        //启动线程
        for(int i=0;i<threadNum;i++){
            threads[i].start();
        }
    }

}
