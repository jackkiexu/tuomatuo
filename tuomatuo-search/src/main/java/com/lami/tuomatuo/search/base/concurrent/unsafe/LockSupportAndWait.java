package com.lami.tuomatuo.search.base.concurrent.unsafe;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xujiankang on 2016/5/31.
 */
public class LockSupportAndWait {

    private static Logger logger = Logger.getLogger(LockSupportAndWait.class);

    public static void goNotify(Thread t){
        synchronized (t){
            t.notify();
        }
    }

    public static void main(String[] args) throws Exception{
        Thread t = new Thread(){
          public void run(){
              try {
                  logger.info("wait");

                  synchronized (this){
                      this.wait();
                  }
                  logger.info("notify work");
                  logger.info("==========================");
                  logger.info("park");

                  // 这里两次调用 park
                  LockSupport.park();
                  LockSupport.park();
                  logger.info("unpark work");
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
        };

        t.start();
        TimeUnit.MILLISECONDS.sleep(100);
        logger.info("go to unpark");
        LockSupport.unpark(t);

        logger.info("go to notify");
        goNotify(t);

        TimeUnit.MICROSECONDS.sleep(1000);

        logger.info("go to notify");
        goNotify(t);
        logger.info("go to notify");
        LockSupport.unpark(t);
    }

}
