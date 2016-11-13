package com.lami.tuomatuo.search.base.concurrent.ReadWriteLock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xjk on 11/12/16.
 */
public class ReadWriteDemo {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        int threadNum = 2;
        ReadWriteDemo demo = new ReadWriteDemo();
        final RWMap rwMap = new RWMap();

        // 开启threadNum个读取线程
        for(int i = 0; i < threadNum; i++){
            new Thread(new Runnable() {
                public void run() {
                    int j = 0;
                    while(j++ < 2){
                        rwMap.get(j);
                    }
                }
            }, "读 Thread " + i){}.start();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 开启threadNum个读取线程
        for(int i = 0; i < threadNum; i++){
            new Thread(new Runnable() {
                public void run() {
                    int j = 0;
                    while(j++ < 3){
                        rwMap.put(j, j+"");
                    }
                }
            }, "写 Thread " + i){}.start();
        }
    }


    static  class RWMap{

        private Map<Integer, String> map;
        private ReadWriteLock rwLock;
        private Lock readLock;
        private Lock writeLock;

        public RWMap() {
            this.map = new HashMap<Integer, String>();
            rwLock = new ReentrantReadWriteLock();
            readLock = rwLock.readLock();
            writeLock = rwLock.writeLock();
            initMap();
        }

        // 初始化Map
        private void initMap(){
            int len = 10;
            for(int i = 0; i < len; i++){
                map.put(i, i + "");
            }
        }

        public String get(int key){
            readLock.lock();
            System.out.println(sdf.format(new Date()) + " " + Thread.currentThread().getName() + " 正在读取map中key=" + key + "的数据");

            try {
                String value = map.get(key);
                Thread.sleep(1000); // 一定的时间间隔
                System.out.println(sdf.format(new Date()) + " " + Thread.currentThread().getName() + "读取的数据内容为:" + value);
                return value;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                readLock.unlock();
            }
            return null;
        }

        public void put(int key, String value){
            writeLock.lock();
            try {
                System.out.println(sdf.format(new Date()) + " " + Thread.currentThread().getName() + " 正在将键值对(key,value)=(" + key + "," + value + ")写入Map中");
                map.put(key, value);
                Thread.sleep(1000); // 一定时间间隔
                System.out.println(sdf.format(new Date()) + " " + Thread.currentThread().getName() + " 写入数据结束");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        }
    }

}
