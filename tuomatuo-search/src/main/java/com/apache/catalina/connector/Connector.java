package com.apache.catalina.connector;

import com.apache.catalina.LifecycleException;
import com.apache.catalina.util.LifecycleMBeanBase;

/**
 * Created by xjk on 3/9/17.
 */
public class Connector extends LifecycleMBeanBase {
    @Override
    protected String getDomainInternal() {
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return null;
    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }
}
