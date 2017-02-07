package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import org.apache.log4j.Logger;

import java.util.concurrent.CyclicBarrier;

/**
 * Created by xujiankang on 2017/2/7.
 */
public class CyclicBarrierDemo3 {

    private static final Logger logger = Logger.getLogger(CyclicBarrierDemo3.class);

     static final class Task extends Thread{

         private CyclicBarrier barrier;

         public Task(CyclicBarrier barrier) {
             this.barrier = barrier;
         }

         @Override
         public void run() {
            logger.info(Thread.currentThread().getName() + " 初始化开始.....");

         }
     }

}
