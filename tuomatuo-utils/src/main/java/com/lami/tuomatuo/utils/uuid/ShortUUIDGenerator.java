package com.lami.tuomatuo.utils.uuid;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class ShortUUIDGenerator implements UUIDGenerator
{

    private static final int IP; // 本机ip地址(pad 后)

    private static AtomicInteger counter = new AtomicInteger(0);

    private static final int JVM_TIME = (int) (System.currentTimeMillis() >>> 8);

    static {
        int ipadd = 0;
        try {
            byte[] addr = InetAddress.getLocalHost().getAddress();
            for (int i = 0; i < 4; i++) {
                ipadd = (ipadd << 8) - Byte.MIN_VALUE + addr[i];
            }
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    public CharSequence generate() {
        return new StringBuilder(32)
                .append(format(getIP()))// 8 位
                .append(format(getJvmTime()))// 8 位
                .append(format(getHighTime()))// 4 位
                .append(format(getLowTime()))// 8 位
                .append(format(getCount()));// 4 位
    }

    private static int getJvmTime() {
        return ShortUUIDGenerator.JVM_TIME;
    }

    private static short getCount() {
        counter.compareAndSet(Short.MAX_VALUE, 0);
        return (short) counter.incrementAndGet();
    }

    private static int getIP() {
        return ShortUUIDGenerator.IP;
    }
    private static short getHighTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }
    private static int getLowTime() {
        return (int) System.currentTimeMillis();
    }

    private static String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    private static String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
}
