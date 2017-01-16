package com.lami.tuomatuo.search.base.concurrent.thread;

import org.apache.log4j.Logger;

/**
 * Created by xjk on 1/13/17.
 */
public class DaemonThreadTest {

    private static final Logger logger = Logger.getLogger(DaemonThreadTest.class);

    static Thread t1 = new Thread(){
        @Override
        public void run() {
            while(true){
               logger.info("I am alive");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            logger.info("我要退出程序了");
        }
    };

    public static void main(String[] args) throws Exception{
        t1.setDaemon(true);
        t1.start();

        Thread.sleep(3 * 1000);

        logger.info("main 方法执行OK");

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        logger.info("DaemonThreadTest 退出");
    }

}
