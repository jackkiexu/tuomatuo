package com.lami.tuomatuo.search.base.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/5/9.
 */
public class Counter {

    public static volatile AtomicInteger count = new AtomicInteger(0);

    public static void inc(){
        // 这里延迟1毫秒, 使得结果明显
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count.getAndIncrement();
    }

    public static void main(String[] args) {
        // 同时启动1000个线程, 去进行i++计算, 看看实际结果
        for(int i = 0; i < 1000; i++){
            new Thread(new Runnable(){
                public void run(){
                    Counter.inc();
                }
            }).start();
        }

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Counter.count:" + Counter.count);
    }

}
