package com.lami.tuomatuo.mq.base.netty.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xujiankang on 2016/9/26.
 */
public class TimeBaseUuidGenerator {

    private static AtomicInteger SEQUENCE = new AtomicInteger((int)System.nanoTime());
    private static long NODE;

    static {
        // Generate nodeKey - we can't use MAC address to support Java 5
        StringBuilder nodeKey = new StringBuilder(1024);

        // Append
    }

}
