package com.apache.catalina;

import javax.management.MBeanRegistration;
import javax.management.ObjectName;

/**
 * This interface is implemented by components that will be registered with an
 * MBean server when they are created and unregistered when they are destroyed.
 * It is primarily intended to be implemented by components that implement
 * {@link Lifecycle} but is not exclusively for them
 *
 * Created by xjk on 3/6/17.
 */
public interface JmxEnabled extends MBeanRegistration {

    /**
     * Obtain the domain under which this component will be/has been
     * registered
     * @return
     */
    String getDomain();

    /**
     * Specify the domain under which this component should be registered. Used
     * with components that cannot (easily) navigate the component hierachy to
     * determine the correct domain to use.
     *
     * @param domain
     */
    void setDomain(String domain);

    /**
     * Obtain the name under which this component has been registered with JMX
     * @return
     */
    ObjectName getObjectName();
}
