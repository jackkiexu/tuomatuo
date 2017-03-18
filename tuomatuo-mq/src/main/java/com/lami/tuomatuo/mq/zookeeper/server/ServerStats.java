package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.common.Time;

/**
 * Basic Server Statistics
 *
 * Created by xujiankang on 2017/3/17.
 */
public class ServerStats {

    private long packetsSent;
    private long packetsReceived;
    private long maxLatency;
    private long minLatency = Long.MAX_VALUE;
    private long totalLatency = 0;
    private long count = 0;

    private final Provider provider;

    public interface Provider {
        public long getOutstandingRequests();
        public long getLastProcessedZxid();
        public String getState();
        public int getNumAliveConnections();
        public long getDataDirSize();
        public long getLogDirSize();
    }

    public ServerStats(Provider provider) {
        this.provider = provider;
    }

    synchronized public long getMinLatency() {
        return minLatency == Long.MAX_VALUE ? 0 : minLatency;
    }

    synchronized public long getAvgLatency(){
        if(count != 0){
            return totalLatency / count;
        }
        return 0;
    }

    synchronized public long getMaxLatency(){
        return maxLatency;
    }

    public long getOutstandingRequests(){
        return provider.getOutstandingRequests();
    }

    public long getLastProcessedZxid(){
        return provider.getLastProcessedZxid();
    }

    public long getDataDirSize(){
        return provider.getDataDirSize();
    }

    public long getLogDirSize(){
        return provider.getLogDirSize();
    }

    synchronized public long getPacketsReceived(){
        return packetsReceived;
    }

    synchronized public long getPacketsSent(){
        return packetsSent;
    }

    public String getServerState(){
        return provider.getState();
    }

    /** The number of client connections alive to this server */
    public int getNumAliveClientConnections(){
        return provider.getNumAliveConnections();
    }

    public boolean isProviderNull(){
        return provider == null;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Latency min/avg/max: " + getMinLatency() + "/"
                + getAvgLatency() + "/" + getMaxLatency() + "\n");
        sb.append("Received: " + getPacketsReceived() + "\n");
        sb.append("Sent: " + getPacketsSent() + "\n");
        sb.append("Connections: " + getNumAliveClientConnections() + "\n");

        if (provider != null) {
            sb.append("Outstanding: " + getOutstandingRequests() + "\n");
            sb.append("Zxid: 0x"+ Long.toHexString(getLastProcessedZxid())+ "\n");
        }
        sb.append("Mode: " + getServerState() + "\n");
        return sb.toString();
    }

    synchronized void updateLatency(long requestCreateTime){
        long latency = Time.currentElapsedTime() - requestCreateTime;
        totalLatency += latency;
        count++;
        if(latency < minLatency){
            minLatency = latency;
        }
        if(latency > maxLatency){
            maxLatency = latency;
        }
    }

    synchronized public void resetLatency(){
        totalLatency = 0;
        count = 0;
        maxLatency = 0;
        minLatency = Long.MAX_VALUE;
    }

    synchronized public void resetMaxLatency(){
        maxLatency = getMinLatency();
    }

    synchronized public void incrementPacketsReceived(){
        packetsReceived++;
    }
    synchronized public void incrementPacketsSent(){
        packetsSent++;
    }

    synchronized public void resetRequestsCounters(){
        packetsReceived = 0;
        packetsSent = 0;
    }

    synchronized public void reset(){
        resetLatency();
        resetRequestsCounters();
    }




}
