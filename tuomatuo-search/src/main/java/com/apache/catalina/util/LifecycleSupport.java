package com.apache.catalina.util;

import com.apache.catalina.Lifecycle;
import com.apache.catalina.LifecycleEvent;
import com.apache.catalina.LifecycleListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Support class to assist in firing LifecycleEvent notifications to
 * registered LifecycleListeners
 *
 * Created by xjk on 3/12/17.
 */
public class LifecycleSupport {

    /**
     * Construct a new LifecycleSupport object associated with the specified
     * Lifecycle component
     *
     * @param lifecycle The Lifecycle component that will be the source
     *                  of events that we fire
     */
    public LifecycleSupport(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    /**
     * The source component for lifecycle events that we will fire;
     */
    private final Lifecycle lifecycle;


    /**
     * The list of registered LifecycleListeners for event notifications
     */
    private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();


    /**
     * add a lifecycle event listner to this component
     * @param listener
     */
    public void addLifecycleListener(LifecycleListener listener){
        listeners.add(listener);
    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned
     * @return
     */
    public LifecycleListener[] findLifecycleListeners(){
        return listeners.toArray(new LifecycleListener[0]);
    }

    /**
     * Notify all lifecycle event listeners that a particular event has
     * occured for this Container. The default implementation performs
     * this notification synchronously using the calling thread
     *
     * @param type
     * @param data
     */
    public void fireLifecycleEvent(String type, Object data){
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        for(LifecycleListener listener : listeners){
            listener.lifecycleEvent(event);
        }
    }

    /**
     * Remove a lifecycle event listener from this component
     *
     * @param listener The listener to remove
     */
    public void removeLifecycleListener(LifecycleListener listener){
        listeners.remove(listener);
    }

}
