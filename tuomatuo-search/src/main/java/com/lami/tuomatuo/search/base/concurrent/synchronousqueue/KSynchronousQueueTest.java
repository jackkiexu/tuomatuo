package com.lami.tuomatuo.search.base.concurrent.synchronousqueue;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by xujiankang on 2016/12/27.
 */
public class KSynchronousQueueTest {

    private static final Logger logger = Logger.getLogger(KSynchronousQueueTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        KSynchronousQueue<Object> queue = new KSynchronousQueue<Object>(true);
        List<Thread> list = new ArrayList<>();
        for(int i=0;i<3;i++){
            Thread t = new SQThread(queue, 1, "SynchronousQueueTest Thread :" + i);
            t.start();
            list.add(t);
        }


        Thread.sleep(10 * 1000);

        for(Thread thread : list){
//            Thread.sleep(3*1000);
            thread.interrupt();
        }
        for(int i=0;i<1;i++){
//            queue.put("" + i);
           /* if(!){
                logger.info("Failure");
            }else{
                logger.info("queue.offer success " + i);
            }*/
        }
    }
    public static class SQThread extends Thread{
        private KSynchronousQueue<Object> queue;
        int mode;
        SQThread(KSynchronousQueue<Object> queue,int mode, String name){
            super(name);
            this.queue = queue;
            this.mode = mode;
        }
        @Override
        public void run(){
            Object item = null;
            try{
                if(mode == 1){
                    logger.info("consumer begin to consumer, but he need sleep");
                    Thread.sleep(1 * 1000);
                    logger.info("consumer begin to consumer, but he need sleep, and he is OK");
                    while((item = queue.take()) != null){
                        Thread.sleep(1000*1000);
                        logger.info("getitem.toString():"+item.toString());
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
