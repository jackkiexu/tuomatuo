package com.lami.tuomatuo.search.base.concurrent.copyonwritearraylist;

import org.apache.log4j.Logger;

/**
 * Created by xjk on 1/26/17.
 */
public class BreakTest {

    private static final Logger logger = Logger.getLogger(BreakTest.class);

    public static void main(String[] args) {
        int a = 1;
        int b = 2;

        if(a < b) breakOne:{
            int i = 0;
            for(logger.info("for loop init code"); i < 3; i++){
                logger.info("logger i : " + i);
                if(i == 2) break breakOne;
                logger.info("i > 2 i:" + i);
            }
            logger.info("for loop over");
        }

        logger.info("if(a < b) over");
    }

}
