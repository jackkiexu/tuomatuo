package com.lami.tuomatuo.mq.zookeeper.jmx;

/**
 * ZooKeeper MBean info interface. MBeanRegistry uses the interface to generate
 * JMX object name.
 * Created by xjk on 3/16/17.
 */
public interface ZKMBeanInfo {

    /**
     * A string identifying the MBean
     * @return
     */
    public String getName();

    /**
     * If isHidden returns true, the MBean won't be registered with MBean server
     * and thus won't be available for management tools. Used for grouping MBeans
     *
     * @return true if the MBean is hidden
     */
    public boolean isHidden();
}
