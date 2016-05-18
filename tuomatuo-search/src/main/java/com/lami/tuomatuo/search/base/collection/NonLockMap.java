package com.lami.tuomatuo.search.base.collection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/5/4.
 */
public class NonLockMap {

    public static void main(String[] args) {
        addCounter();
        System.out.println("addCounter:" + counter.toString());
    }


    private static AtomicInteger counter = new AtomicInteger(3);

    public static void addCounter(){
        for(;;){
            int oldValue = counter.get();
            int newValue = oldValue + 1;
            if(counter.compareAndSet(oldValue, newValue)){
                return ;
            }
        }
    }
}
