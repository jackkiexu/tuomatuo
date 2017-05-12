package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * ZooKeeper data tree MBean
 * Created by xujiankang on 2017/3/19.
 */
public interface DataTreeMXBean {

    /**
     * number of znodes in the data tree
     * @return
     */
    public int getNodeCount();

    /**
     * number of watches set
     * @return
     */
    public String getLastZxid();

    /**
     * data tree size in bytes. The size includes the znode path and
     * its value
     * @return
     */
    public long approximateDataSize();

    /**
     * number of ephemeral nodes in the data tree
     * @return
     */
    public int countEphemerals();
}
