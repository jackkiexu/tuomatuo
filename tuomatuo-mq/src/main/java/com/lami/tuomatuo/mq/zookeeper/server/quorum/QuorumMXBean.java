package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * An MBean representing a zookeeper cluster nodes(aka quorum peers)
 * Created by xujiankang on 2017/3/19.
 */
public interface QuorumMXBean {

    /**
     * the name of the quorum
     * @return
     */
    public String getname();

    /**
     * configured number of peers in the quporum
     * @return
     */
    public int getQuorumSize();
}
