package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.CustomerClass;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.extractor.ExcelExtractor;

/**
 * Created by xujiankang on 2017/1/16.
 */
public class KConcurrentLinkedListTest {

    private static final Logger logger = Logger.getLogger(KConcurrentLinkedListTest.class);

    public static void main(String[] args) throws Exception{
        KConcurrentLinkedList<CustomerClass.Person> queue = new KConcurrentLinkedList<>();

        for(int i = 0; i<10; i++){
            CustomerClass.Person person = new CustomerClass.Person();
            person.setId(i);
            person.setName("name " + i);
            queue.offer(person);
        }

        Thread t1 = new Thread(){
            @Override
            public void run() {
                CustomerClass.Person person = queue.poll("1");
            }
        };

        Thread t2 = new Thread(){
            @Override
            public void run() {
                int i = 0;
                while(i< 5) {
                    CustomerClass.Person person = queue.poll();
                    logger.info(person);
                    if (person == null) break;
                    i++;
                }
            }
        };

        t1.start();
        Thread.sleep(1*1000);
        t2.start();


    }
}
