package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * Implementation of connection MBean interface
 * s
 * Created by xujiankang on 2017/3/19.
 */
public class Connectionbean implements ConnectionMXBean, ZKMBeanInfo {
    @Override
    public String getSourceIP() {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public String getStartedTime() {
        return null;
    }

    @Override
    public String[] getEphemeralNodes() {
        return new String[0];
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
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void terminateSession() {

    }

    @Override
    public void terminateConnection() {

    }

    @Override
    public long getMinLatency() {
        return 0;
    }

    @Override
    public long getAvgLatency() {
        return 0;
    }

    @Override
    public long getMaxLatency() {
        return 0;
    }

    @Override
    public String getLastOperation() {
        return null;
    }

    @Override
    public String getLastCxid() {
        return null;
    }

    @Override
    public String getLastZxid() {
        return null;
    }

    @Override
    public String getLastResponseTime() {
        return null;
    }

    @Override
    public long getLastLatency() {
        return 0;
    }

    @Override
    public void resetCounters() {

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
