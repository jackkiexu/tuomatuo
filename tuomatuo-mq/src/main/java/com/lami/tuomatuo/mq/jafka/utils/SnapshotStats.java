package com.lami.tuomatuo.mq.jafka.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by xjk on 2016/9/30.
 */
public class SnapshotStats {

    Time time = Time.SystemTime;

    private long monitorDurationNs;

    private final AtomicReference<Stats> complete = new AtomicReference<Stats>(new Stats());

    private final AtomicReference<Stats> current = new AtomicReference<Stats>(new Stats());

    private final AtomicLong total = new AtomicLong(0);

    private final AtomicLong numCumulatedRequests = new AtomicLong(0);

    public SnapshotStats(long monitorDurationNs) {
        this.monitorDurationNs = monitorDurationNs;
    }

    public SnapshotStats() {
        this(TimeUnit.MINUTES.toNanos(10));
    }

    public long getTotalMetric(){
        return total.get();
    }

    public double getMaxMetric(){
        return complete.get().maxRequestMetric;
    }

    public void recordRequestMetric(long requestsNs){
        Stats stats = current.get();
        stats.add(requestsNs);
        total.getAndAdd(requestsNs);
        numCumulatedRequests.getAndAdd(1);
        long ageNs = time.nanoseconds() - stats.start;
        // if the current stats are too old it is time swap
        if(ageNs >= monitorDurationNs){
            boolean swapped = current.compareAndSet(stats, new Stats());
            if(swapped){
                complete.set(stats);
                stats.end.set(time.nanoseconds());
            }
        }
    }

    public void recordThroughputMetric(long data){
        Stats stats = current.get();
        stats.addData(data);
        long ageNs = time.nanoseconds() - stats.start;
        // if the current stats are too old it is time to swap
        if(ageNs >= monitorDurationNs){
            boolean swapped = current.compareAndSet(stats, new Stats());
            if(swapped){
                complete.set(stats);
                stats.end.set(time.nanoseconds());
            }
        }
    }

    public long getNumRequests(){
        return numCumulatedRequests.get();
    }

    public double getRequestsPerSecond(){
        Stats stats = complete.get();
        return stats.numRequests / stats.durationSeconds();
    }

    public double getThroughput(){
        Stats stats = complete.get();
        return stats.numRequests / stats.durationSeconds();
    }

    public double getAvgMetric(){
        Stats stats = complete.get();
        if(stats.numRequests == 0){
            return 0;
        }else{
            return stats.totalRequestMetric / stats.numRequests;
        }
    }

    // =========================================



    class Stats{
        long start = time.nanoseconds();

        AtomicLong end = new AtomicLong(-1);

        int numRequests = 0;

        long totalRequestMetric = 0l;

        long maxRequestMetric = 0l;

        long totalData = 0l;


        private Object lock = new Object();

        long addData(long data){
            synchronized (lock){
                totalData += data;
                return totalData;
            }
        }

        long add(long requestNs){
            synchronized (lock){
                numRequests += 1;
                totalRequestMetric += requestNs;
                maxRequestMetric = Math.max(maxRequestMetric, requestNs);
                return maxRequestMetric;
            }
        }

        double durationSeconds(){
            return (end.get() - start) / (1000.0 * 1000.0 * 1000.0);
        }

        double durationMs(){
            return (end.get() - start) / (1000.0 * 1000.0);
        }

    }
}
