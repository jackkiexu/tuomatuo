package com.lami.tuomatuo.mq.base.netty.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
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

        // Append host/IP address information
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            nodeKey.append(localhost.getCanonicalHostName());
            nodeKey.append(":");
            nodeKey.append(String.valueOf(localhost.getHostAddress()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UUID generate() {
        long time = System.currentTimeMillis();
        int clockSeq = TimeBaseUuidGenerator.SEQUENCE.getAndIncrement();

        long msb = (time & 0xFFFFFFFFL) << 32 | (time >>> 32 & 0xFFFF) << 16 |
                time >>> 48 & 0xFFFF;
        long lsb = (long) clockSeq << 48 | NODE;

        // Set to version 1 (i.e. time-based UUID)
        msb = msb & 0xFFFFFFFFFFFF0FFFL | 0x0000000000001000L;

        // Set to IETF variant
        lsb = lsb & 0x3FFFFFFFFFFFFFFFL | 0x8000000000000000L;

        return new UUID(msb, lsb);
    }

}
