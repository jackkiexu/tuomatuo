package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * This class implements the data tree MBean
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DataTreeBean  implements DataTreeMXBean, ZKMBeanInfo{

    public DataTree dataTree;

    public DataTreeBean(DataTree dataTree) {
        this.dataTree = dataTree;
    }

    @Override
    public int getNodeCount() {
        return dataTree.getNodeCount();
    }

    @Override
    public long approximateDataSize() {
        return dataTree.approximateDataSize();
    }

    @Override
    public int countEphemerals() {
        return dataTree.getEphemeralsCount();
    }

    public int getWatchCount(){
        return dataTree.getWatchCount();
    }

    @Override
    public String getName() {
        return "InMemoryDataTree";
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public String getLastZxid() {
        return "0x" + Long.toHexString(dataTree.lastProcessedZxid);
    }
}
