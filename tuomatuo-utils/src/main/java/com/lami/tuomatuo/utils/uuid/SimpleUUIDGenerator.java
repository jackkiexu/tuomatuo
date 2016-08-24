package com.lami.tuomatuo.utils.uuid;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成一个 128-bit 的 UUID 字符串类型，这在一个网络中是唯一的（使用了 IP 地址）。
 * <p/>
 * UUID 被编码为一个 32 位 16进制数字表示的字符串。
 * <p/>
 * UUID 包含：IP 地址(保证网络唯一)、JVM 的启动时间（精确到 1/4 秒,保证单个jvm唯一）、系统时间和一个计数器值（在 JVM 中 唯一）。
 * <p/>
 * 提取自hibernate.
 * <p/>
 * Date: 13-9-9 - Time: 上午12:21
 *
 * @author bo.wen
 */
public class SimpleUUIDGenerator implements IDGenerator
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
        return SimpleUUIDGenerator.JVM_TIME;
    }
    private static short getCount() {
        counter.compareAndSet(Short.MAX_VALUE, 0);
        return (short) counter.incrementAndGet();
    }

    private static int getIP() {
        return SimpleUUIDGenerator.IP;
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
