package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * keadeer election prorocol MBean
 * Created by xujiankang on 2017/3/19.
 */
public interface LeaderElectionMXBean {

    /**
     * the time when the leader election started
     * @return
     */
    public String getStartTime();

}
