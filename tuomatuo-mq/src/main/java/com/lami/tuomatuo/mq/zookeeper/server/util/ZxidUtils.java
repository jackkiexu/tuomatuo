package com.lami.tuomatuo.mq.zookeeper.server.util;

/**
 * Created by xjk on 3/17/17.
 */
public class ZxidUtils {
    static public long getEpochFromZxid(long zxid) { return zxid >> 32L; }
    static public long getCounterFromZxid(long zxid) { return zxid & 0xffffffffL; }
    static public long makeZxid(long epoch, long counter) { return (epoch << 32L) | (counter & 0xffffffffL);}
    static public String zxidToString(long zxid) { return Long.toHexString(zxid); }
}
