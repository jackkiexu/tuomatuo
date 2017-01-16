package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/1/16.
 */
public class Loop<E> {

    private static final Logger logger = Logger.getLogger(Loop.class);

    public static void main(String[] args) {
        String tail = "";
        String t = (tail = "oldTail");
        tail = "newTail";
        boolean isEqual = t != (t = tail); // <- 神奇吧
        System.out.println("isEqual : "+isEqual);
        // isEqual : true

        int w = 0;
        restartFromHead:
        for(System.out.println("xx");;){
            w++;
            if (w > 2) break ;
            for(int i = 0; i < 10; i++){
                if (i == 8) continue restartFromHead;
                System.out.println("print i : " + i);
            }
        }

        System.out.println("over");

        w = 0;
        restartFromHead2:
        for(;;){  // <- 为什么两个 for 循环
            for(System.out.println("init for ");;){
                w++;
                if (w > 2) break ;
                for(int i = 0; i < 10; i++){
                    if (i == 8) continue restartFromHead2;
                    System.out.println("print i : " + i);
                }
            }
        }
    }

}
