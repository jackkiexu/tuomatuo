package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * A quorum server MBean
 * Created by xujiankang on 2017/3/19.
 */
public interface ServerMXBean {

    /**
     * name of the server MBean
     * @return
     */
    public String getName();

    /**
     * the start time the server
     * @return
     */
    public String getStartTime();

}
