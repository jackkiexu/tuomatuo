package com.lami.tuomatuo.mq.zookeeper.jmx;

import org.apache.zookeeper.jmx.MBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;

/**
 * Shared utilities
 * Created by xjk on 3/16/17.
 */
public class ManageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ManageUtil.class);

    private static final boolean isLog4jJmxEnabled(){
        boolean enabled = false;

        try{
            Class.forName("org.apache.log4j.spi.LoggerRepository");
            if(Boolean.getBoolean("zookeeper.jmx.log4j.disable") == true){
                LOG.info("Log4j found but jmx support is disabled");
            }else{
                enabled = true;
                LOG.info("Log4j found with jmx enabled");
            }
        }catch (ClassNotFoundException e){
            LOG.info("Log4j not found");
        }

        return enabled;
    }

    public static void registerLog4jMBeans() throws JMException{
        if(isLog4jJmxEnabled()){
            LOG.debug("registerLog4jMBeans()");
            MBeanServer mbs = MBeanRegistry.getInstance().getPlatformMBeanServer();

            try{
                // Create and Register the top level Log4J MBean
                // org.apache.log4j.jmx.HierachyDynamicMBean hdm = new org.apache.log4j.jmx.HierarchyDynamicMBean();
            }catch (Exception e){
                LOG.error("Problems while registering log4j jmx beans!", e);
                throw new JMException(e.toString());
            }

        }
    }

}
