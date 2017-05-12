package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

import java.util.Date;

/**
 * An abstract base class for the leader and follower MBeans
 * Created by xujiankang on 2017/3/19.
 */
public abstract class ServerBean implements ServerMXBean, ZKMBeanInfo {

    private final Date startTime = new Date();

    public boolean isHidden() {
        return false;
    }

    public String getStartTime(){
        return startTime.toString();
    }

}
