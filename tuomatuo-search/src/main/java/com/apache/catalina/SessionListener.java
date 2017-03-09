package com.apache.catalina;

import java.util.EventListener;

/**
 * Interface defining a listener for significant Session generated events
 *
 * Created by xjk on 3/6/17.
 */
public interface SessionListener extends EventListener {

    /**
     * Acknowledge the occurrence of the specified event
     */
    void sessionEvent(SessionEvent event);
}
