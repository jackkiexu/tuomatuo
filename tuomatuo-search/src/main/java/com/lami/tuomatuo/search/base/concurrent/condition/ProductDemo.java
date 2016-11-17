package com.lami.tuomatuo.search.base.concurrent.condition;

/**
 * Created by xjk on 11/16/16.
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProductDemo {

    private Lock lock = new ReentrantLock();
    private Condition nonFull = lock.newCondition();
    private Condition nonEmpty = lock.newCondition();
    private Object [] items;
    private int head,tail,count;

    public ProductDemo(int maxSize){
        items = new Object[maxSize];
        count = 0;
        head = tail = 0;
    }
    public ProductDemo(){
        this(100);
    }
    public void put(Object o) throws InterruptedException{
        lock.lock();

        try{
            while(count==items.length){
                nonFull.await();
            }
            items[tail++]=o;
            if(tail==items.length){
                tail = 0;
            }
            count++;
            nonEmpty.signalAll();
        }finally{
            lock.unlock();
        }

    }

    public Object take() throws InterruptedException{
        lock.lock();

        try{
            while(count<=0){
                nonEmpty.await();
            }
            count --;
            Object o = items[head];
            head++;
            if(head==items.length){
                head = 0;
            }
            nonFull.signalAll();
            return o;
        }finally{
            lock.unlock();
        }
    }


}