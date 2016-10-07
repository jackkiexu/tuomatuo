package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.api.RequestKeys;
import com.lami.tuomatuo.mq.jafka.utils.SnapshotStats;
import com.lami.tuomatuo.mq.jafka.utils.Time;

/**
 * Created by xjk on 10/1/16.
 */
public class SocketServerStats implements SocketServerStatsMbean, IMBeanName {

    long monitorDurationNs;
    Time time;

    SnapshotStats produceTimeStats;

    SnapshotStats fetchTimeStats;

    SnapshotStats produceBytesStats;

    SnapshotStats fetchBytesStats;


    public SocketServerStats(long monitorDurationNs, Time time) {
        this.monitorDurationNs = monitorDurationNs;
        this.time = time;
        produceTimeStats = new SnapshotStats(monitorDurationNs);
        fetchTimeStats = new SnapshotStats(monitorDurationNs);
        produceBytesStats = new SnapshotStats(monitorDurationNs);
        fetchBytesStats = new SnapshotStats(monitorDurationNs);
    }

    public SocketServerStats(long monitorDurationNs){
        this(monitorDurationNs, Time.SystemTime);
    }

    public void recordBytesRead(int bytes){
        produceBytesStats.recordRequestMetric(bytes);
    }

    public void recordRequest(RequestKeys requestTypeId, long durationNs){
        switch (requestTypeId){
            case Produce:
            case MultiProduce:
                produceTimeStats.recordRequestMetric(durationNs);
                break;
            case Fetch:
            case MultiFetch:
                fetchTimeStats.recordRequestMetric(durationNs);
            default:
                break;
        }
    }

    public void recordBytesWritten(int bytes){
        fetchBytesStats.recordRequestMetric(bytes);
    }


    public String getMbeanName() {
        return "jafka.type=jafka.SocketServerStats";
    }

    public double getProduceRequestsPerSecond() {
        return produceTimeStats.getRequestsPerSecond();
    }

    public double getFetchRequestsPerSecond() {
        return fetchTimeStats.getRequestsPerSecond();
    }

    public double getAvgProduceRequestMs() {
        return produceTimeStats.getAvgMetric() / (1000.0 * 1000.0);
    }

    public double getMaxProduceRequestMs(){
        return produceTimeStats.getMaxMetric() / (1000.0 * 1000.0);
    }

    public double getAvgFetchRequestMs(){
        return fetchTimeStats.getAvgMetric() / (1000.0 * 1000.0);
    }

    public double getMaxFetchRequestMs(){
        return fetchTimeStats.getMaxMetric() / (1000.0 * 1000.0);
    }





    public double getMaxProduceRequestsMs() {
        return 0;
    }

    public double getAvgFetchRequestsMs() {
        return 0;
    }

    public double getMaxFetchRequestsMs() {
        return 0;
    }

    public double getBytesReadPerSecond() {
        return produceBytesStats.getAvgMetric();
    }

    public double getBytesWrittenPerSecond() {
        return fetchBytesStats.getAvgMetric();
    }

    public long getNumFetchRequests() {
        return fetchTimeStats.getNumRequests();
    }

    public long getNumProduceRequests() {
        return produceTimeStats.getNumRequests();
    }

    public long getTotalBytesRead(){
        return produceBytesStats.getTotalMetric();
    }

    public long getTotalByteRead() {
        return 0;
    }

    public long getTotalBytesWritten() {
        return fetchBytesStats.getTotalMetric();
    }

    public long getTotalFetchRequestMs() {
        return fetchTimeStats.getTotalMetric();
    }

    public long getTotalProduceRequestMs() {
        return produceTimeStats.getTotalMetric();
    }
}
