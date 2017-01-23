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

        // 初始化 6 个 consumer
        List<Thread> list = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Thread t = new SQThread(queue, 1, "SynchronousQueueTest Thread :" + i);
            t.start();
            list.add(t);
        }

        Thread.sleep(2 * 1000);

        // 初始化 一个 producer
        Thread t = new SQThread(queue, 0, "SynchronousQueueTest Thread producer", "A");
        t.start();

        // 第一个 concumer 配对成功, 将第三个中段
        list.get(3).interrupt();


        Thread.sleep(2 * 1000);
        list.get(5).interrupt();

    }

    public static class SQThread extends Thread{

        private int mode;
        private Object value;
        private KSynchronousQueue<Object> queue;

        SQThread(KSynchronousQueue<Object> queue,int mode, String name){
            super(name);
            this.queue = queue;
            this.mode = mode;
        }

        SQThread(KSynchronousQueue<Object> queue,int mode, String name, Object value){
            super(name);
            this.queue = queue;
            this.mode = mode;
            this.value = value;
        }

        @Override
        public void run(){
            Object item = null;
            try{
                // mode == 1 consumer
                if(mode == 1){
                    item = queue.take();
                    logger.info("getitem.toString():"+item.toString());
                } // mode = other
                else{
                    Thread.sleep(1 * 1000);
                    queue.put(value);
                    Thread.sleep(2 * 1000);
                }
            }catch(Exception e){
                //
            }
            logger.info(Thread.currentThread().getName() + " execute over");
        }
    }

}
