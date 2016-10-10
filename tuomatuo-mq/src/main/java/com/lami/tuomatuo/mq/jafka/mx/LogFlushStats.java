package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.utils.SnapshotStats;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

/**
 * Created by xjk on 2016/10/10.
 */
public class LogFlushStats implements LogFlushStatsMBean, IMBeanName{

    private final SnapshotStats flushRequestsStats = new SnapshotStats();

    public LogFlushStats() {
    }

    public String getMbeanName() {
        return "jafka:type=jafka.LogFlushStats";
    }

    public double getFlushesPerSecond() {
        return flushRequestsStats.getRequestsPerSecond();
    }

    public double getAvgFlushMs() {
        return flushRequestsStats.getAvgMetric();
    }

    public long getTotalFlushMs() {
        return flushRequestsStats.getTotalMetric();
    }

    public double getMaxFlushMs() {
        return flushRequestsStats.getMaxMetric();
    }

    public long getNumFlushes() {
        return flushRequestsStats.getNumRequests();
    }

    private static class LogFlushStatsHolder{
        static LogFlushStats stats = new LogFlushStats();
        static {
            Utils.registerMBean(stats);
        }
    }

    public static void recordFlushRequest(long requestMs){
        LogFlushStatsHolder.stats.flushRequestsStats.recordRequestMetric(requestMs);
    }
}
