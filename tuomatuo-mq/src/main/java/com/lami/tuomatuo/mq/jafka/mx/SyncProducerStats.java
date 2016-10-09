package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.common.annotations.ThreadSafe;
import com.lami.tuomatuo.mq.jafka.utils.SnapshotStats;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

/**
 * Created by xjk on 2016/10/9.
 */
@ThreadSafe
public class SyncProducerStats implements SyncProducerStatsMBean, IMBeanName {

    private final SnapshotStats produceRequestStats = new SnapshotStats();

    public String getMbeanName() {
        return "jafka:type=jafkaProducerStats";
    }

    public double getProduceRequestsPerSecond() {
        return produceRequestStats.getRequestsPerSecond();
    }

    public double getAvgProduceRequestms() {
        return produceRequestStats.getAvgMetric() / (1000.0 * 1000.0);
    }

    public double getMaxProduceRequestMs() {
        return produceRequestStats.getMaxMetric() / (1000.0 * 1000.0);
    }

    public long getNumProduceRequests() {
        return produceRequestStats.getNumRequests();
    }

    private static class SyncProducerStatsHolder{
        static  SyncProducerStats instance = new SyncProducerStats();
        static {
            Utils.registerMBean(instance);
        }
    }

    public static void recordProducerRequest(long requestMs){
        SyncProducerStatsHolder.instance.produceRequestStats.recordRequestMetric(requestMs);
    }
}
