package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/1/19.
 */
public class Loop {

    private static final Logger logger = Logger.getLogger(Loop.class);

    public static void main(String[] args) throws Exception{

        int i = 0;
        logger.info("init i value");
        for(;;){
            logger.info("reinit i value");
            sleep();
            i = 0;

            for(; i < 100; i++){
//                sleep();
                if( i == 5){
                    logger.info("i value is 5");
                    sleep();
                    break;
                }
            }
        }
    }


    private static void sleep() throws Exception{
        Thread.sleep(1*1000);
    }
}
