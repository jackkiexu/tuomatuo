package com.lami.tuomatuo.search.base.concurrent.thread;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by xjk on 1/13/17.
 */
public class ThreadTest {

    private static final Logger logger = Logger.getLogger(ThreadTest.class);

    public static void main(String[] args) throws Exception{

        Thread t1 = new Thread(){
            @Override
            public void run() {
                while(true){
                    if(Thread.currentThread().isInterrupted()){
                        logger.info("线程中断, 退出loop");
                        break;
                    }

                    try {
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.info("线程在 waiting 状态时收到中断信息");
                        logger.info("此时线程中断标示: " + Thread.currentThread().isInterrupted());
                        // 再次点用线程中断, 这时就又有中断标示
                        Thread.currentThread().interrupt();
                    }

                    Thread.yield();
                }
                logger.info("1. 此时线程中断标示: " + Thread.currentThread().isInterrupted());

                // 再次调用程序阻塞, 看看是否有用
                LockSupport.park(this);
                logger.info("2. 此时线程中断标示: " + Thread.currentThread().isInterrupted());
                try {
                    Thread.currentThread().sleep(5*1000);
                } catch (InterruptedException e) {
                    logger.info("在线程中断时调用sleep 抛异常");
                    e.printStackTrace();
                }


                logger.info("3. 此时线程中断标示: " + Thread.currentThread().interrupted());

            }
        };

        t1.start();
        Thread.sleep(2 *1000);
        t1.interrupt();
    }
}
