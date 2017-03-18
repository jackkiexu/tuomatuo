package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * This class implements the ZooKeeper server MBean interface
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServerBean implements ZooKeeperServerMXBean, ZKMBeanInfo{
    @Override
    public String getClientPort() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getStartTime() {
        return null;
    }

    @Override
    public long getMinRequestLatency() {
        return 0;
    }

    @Override
    public long getAvgRequestLatency() {
        return 0;
    }

    @Override
    public long getMaxRequestLatency() {
        return 0;
    }

    @Override
    public long getPacketsReceived() {
        return 0;
    }

    @Override
    public long getPacketsSent() {
        return 0;
    }

    @Override
    public long getOutstandingRequests() {
        return 0;
    }

    @Override
    public int getTickTime() {
        return 0;
    }

    @Override
    public void setTickTime(int tickTime) {

    }

    @Override
    public int getMaxClientCnxnsPerHost() {
        return 0;
    }

    @Override
    public void setMaxClientCnxnsPerHost(int max) {

    }

    @Override
    public int getMinSessionTimeout() {
        return 0;
    }

    @Override
    public void setMinSessionTimeout(int min) {

    }

    @Override
    public int getMaxSessionTimeout() {
        return 0;
    }

    @Override
    public void setMaxSessionTimeout(int max) {

    }

    @Override
    public void resetStatistics() {

    }

    @Override
    public void resetLatency() {

    }

    @Override
    public void resetMaxLatency() {

    }

    @Override
    public long getNumAliveConnections() {
        return 0;
    }

    @Override
    public long getDataDirSize() {
        return 0;
    }

    @Override
    public long getLongDirSize() {
        return 0;
    }

    @Override
    public String getSecureClientPort() {
        return null;
    }

    @Override
    public String getSecureClientAddress() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
