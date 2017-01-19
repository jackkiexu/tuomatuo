package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

import org.apache.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xujiankang on 2017/1/19.
 */
public class ThreadLocalRandomTest {

    private static final Logger logger = Logger.getLogger(ThreadLocalRandomTest.class);

    public static void main(String[] args) {
        int rnd = KThreadLocalRandom.nextSecondarySeed();

        logger.info(rnd);
        logger.info((rnd & 0x80000001) == 0);
        if ((rnd & 0x80000001) == 0) { // test highest and lowest bits
            int level = 1, max;
            while (((rnd >>>= 1) & 1) != 0)
                ++level;
            logger.info("level:" + level);
        }

    }


}
