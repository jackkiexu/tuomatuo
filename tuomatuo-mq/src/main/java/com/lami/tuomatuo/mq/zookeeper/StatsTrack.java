package com.lami.tuomatuo.mq.zookeeper;

/**
 * a class that represents the stats associated with quotas
 * Created by xujiankang on 2017/3/19.
 */
public class StatsTrack {

    private int count;
    private long bytes;
    private String countStr = "count";
    private String byteStr = "bytes";

    /**
     * a default constructor for
     * stats
     */
    public StatsTrack() {
        this(null);
    }

    public StatsTrack(String stats) {
        if(stats == null){
            stats = "count=-1,bytes=-1";
        }
        String[] split = stats.split(",");
        if(split.length != 2){
            throw new IllegalArgumentException("invalid string " + stats);
        }

        count = Integer.parseInt(split[0].split("=")[1]);
        bytes = Long.parseLong(split[1].split("=")[1]);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public String getCountStr() {
        return countStr;
    }

    public void setCountStr(String countStr) {
        this.countStr = countStr;
    }

    public String getByteStr() {
        return byteStr;
    }

    public void setByteStr(String byteStr) {
        this.byteStr = byteStr;
    }

    @Override
    public String toString() {
        return "StatsTrack{" +
                "count=" + count +
                ", bytes=" + bytes +
                ", countStr='" + countStr + '\'' +
                ", byteStr='" + byteStr + '\'' +
                '}';
    }
}
