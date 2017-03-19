package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * This class implements the data tree MBean
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DataTreeBean  implements DataTreeMXBean, ZKMBeanInfo{

    @Override
    public int getNodeCount() {
        return 0;
    }

    @Override
    public String getLastZxid() {
        return null;
    }

    @Override
    public long approximateDataSize() {
        return 0;
    }

    @Override
    public int countEphemerals() {
        return 0;
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
