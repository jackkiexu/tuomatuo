package com.lami.tuomatuo.search.concurrent.aqs;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by xujiankang on 2017/1/25.
 */
public class AQSTest {

    private static final Logger logger = Logger.getLogger(AQSTest.class);

    public static class AQS extends KAbstractQueuedSynchronizer{
        public void setState(){
            boolean result = compareAndSetState(0, 1);
            logger.info("result : " + result);
        }
    }

    public static class AQST extends AbstractQueuedSynchronizer {
        public void setState(){
            boolean result = compareAndSetState(0, 1);
            logger.info("result : " + result);
        }
    }


    public static void main(String[] args) {
        AQS aqs = new AQS();
        aqs.setState();
    }
}
