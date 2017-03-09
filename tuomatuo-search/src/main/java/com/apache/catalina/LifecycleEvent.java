package com.apache.catalina;

import java.util.EventObject;

/**
 *
 * General event for notifying listener of significant changes on a component
 * that implements the lifecycle interface. In particular, this will be useful
 * on Container, where these events replace the ContextInterceptor concept in
 * Tomcat 3.x
 *
 * Created by xjk on 3/9/17.
 */
public class LifecycleEvent extends EventObject{


    private static final long serialVersionUID = 4628483946826481587L;

    /**
     * Construct a new LifecycleEvent with the specified parameters
     * @param source
     * @param data
     * @param type
     */
    public LifecycleEvent(Object source, Object data, String type) {
        super(source);
        this.data = data;
        this.type = type;
    }

    /**
     * The event data associated with this event
     */
    private final Object data;

    /**
     * The event type this instance represents
     */
    private final String type;



    /**
     * Return the Lifecycle on which this event occurred.
     */
    public Lifecycle getLifecycle() {

        return (Lifecycle) getSource();

    }


    public Object getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
