package com.lami.tuomatuo.search.base.concurrent.synchronization;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/2/9.
 */
public class SynchronizedTest {

    private static final Logger logger = Logger.getLogger(SynchronizedTest.class);


    public static void main(String[] args) {

        final Person person = new Person();

        Thread thread1 = new Thread("thread 1 "){
            @Override
            public void run() {
                for(int i = 0; i < 5; i++){
                    person.setId(i);
                }
                logger.info("thread 1 execute OK");
            }

        };



        Thread thread2 = new Thread("thread 2 "){
            @Override
            public void run() {
                for(int i = 0; i < 5; i++){
                    person.setName(i + "");
                }
                logger.info("thread 2 execute OK");
            }
        };
        thread2.start();
        thread1.start();

    }


   static  class Person{
       public Integer id;
       public String name;


       public Integer getId() {
           return id;
       }

       public synchronized void setId(Integer id) {
           logger.info(Thread.currentThread().getName() + " Person setId begin");

           try {
               Thread.sleep( 5 * 1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           this.id = id;
           logger.info(Thread.currentThread().getName() + "Person setId over");
       }

       public String getName() {
           return name;
       }

       public synchronized void setName(String name) {
           logger.info(Thread.currentThread().getName() + "Person setName begin");

           try {
               Thread.sleep( 5 * 1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           this.name = name;
           logger.info(Thread.currentThread().getName() +  "Person setName over");
       }
   }
}
