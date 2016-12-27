package com.lami.tuomatuo.search.base.concurrent.synchronousqueue;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/12/27.
 */
public class KSynchronousQueueTest {

    private static final Logger logger = Logger.getLogger(KSynchronousQueueTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        KSynchronousQueue<Object> queue = new KSynchronousQueue<Object>();
        for(int i=0;i<5;i++){
            Thread t = new SQThread(queue, 1);
            t.start();
        }
        //Thread.sleep(1000);
        for(int i=0;i<10;i++){
            if(!queue.offer(new Object())){
                logger.info("Failure");
            }
        }
    }
    public static class SQThread extends Thread{
        private KSynchronousQueue<Object> queue;
        int mode;
        SQThread(KSynchronousQueue<Object> queue,int mode){
            this.queue = queue;
            this.mode = mode;
        }
        @Override
        public void run(){
            Object item = null;
            try{
                logger.info(Thread.currentThread().getId());
                if(mode == 1){
                    while((item = queue.take()) != null){
                        logger.info(item.toString());
                    }
                }else{
                    //
                }
            }catch(Exception e){
                //
            }
            logger.info("end");
        }
    }

}
