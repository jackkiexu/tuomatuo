package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

import java.util.Date;

/**
 * leader election MBean interface implementation
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderElectionBean implements LeaderElectionMXBean, ZKMBeanInfo {

    private final Date startTime = new Date();


    @Override
    public String getStartTime() {
        return startTime.toString();
    }

    @Override
    public String getName() {
        return "LeaderElection";
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
