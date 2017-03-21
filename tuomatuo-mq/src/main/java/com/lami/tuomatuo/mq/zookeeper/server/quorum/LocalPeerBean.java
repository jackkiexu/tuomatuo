package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Implementation of the local peer MBean interface
 * Created by xujiankang on 2017/3/19.
 */
public class LocalPeerBean extends ServerBean implements LocalPeerMXBean{
    @Override
    public int getTickTime() {
        return 0;
    }

    @Override
    public int getMaxClientCnxnPerHost() {
        return 0;
    }

    @Override
    public int getMinSessionTimeout() {
        return 0;
    }

    @Override
    public int getMaxSessionTimeout() {
        return 0;
    }

    @Override
    public int getInitLimit() {
        return 0;
    }

    @Override
    public int getSyncLimit() {
        return 0;
    }

    @Override
    public int getTick() {
        return 0;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getQuorumAddress() {
        return null;
    }

    @Override
    public int getElectionType() {
        return 0;
    }

    @Override
    public String getElectionAddress() {
        return null;
    }

    @Override
    public String getClientAddress() {
        return null;
    }

    @Override
    public String getLearnerType() {
        return null;
    }

    @Override
    public long getConfigVersion() {
        return 0;
    }

    @Override
    public String getQuorumSystemInfo() {
        return null;
    }

    @Override
    public boolean isPartOfEnsemble() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }
}
