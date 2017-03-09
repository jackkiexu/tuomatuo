package com.apache.catalina;

/**
 * The list of valid states for components that implement {@link Lifecycle}
 * See {@link Lifecycle} for the state transition diagram
 * Created by xjk on 3/9/17.
 */
public enum LifecycleState {
    NEW(false, null),
    INITIALIZING(false, Lifecycle.BEFORE_INIT_EVENT),
    INITIALIZED(false, Lifecycle.AFTER_INIT_EVENT),
    STARTING_PREP(false, Lifecycle.BEFORE_START_EVENT),
    STARTING(true, Lifecycle.START_EVENT),
    STARTED(true, Lifecycle.AFTER_START_EVENT),
    STOPPING_PREP(true, Lifecycle.BEFORE_STOP_EVENT),
    STOPPING(false, Lifecycle.STOP_EVENT),
    STOPPED(false, Lifecycle.AFTER_STOP_EVENT),
    DESTROYING(false, Lifecycle.BEFORE_DESTROY_EVENT),
    DESTROYED(false, Lifecycle.AFTER_DESTROY_EVENT),
    FAILED(false, null),
    /**
     * @deprecated Unused. Will be removed in Tomcat 8.5.x. The state transition
     *             checking in {@link org.apache.catalina.util.LifecycleBase}
     *             makes it impossible to use this state. The intended behaviour
     *             can be obtained by setting the state to
     *             {@link LifecycleState#FAILED} in
     *             <code>LifecycleBase.startInternal()</code>
     */
    @Deprecated
    MUST_STOP(true, null),
    /**
     * @deprecated Unused. Will be removed in Tomcat 8.5.x. The state transition
     *             checking in {@link org.apache.catalina.util.LifecycleBase}
     *             makes it impossible to use this state. The intended behaviour
     *             can be obtained by implementing {@link Lifecycle.SingleUse}.
     */
    @Deprecated
    MUST_DESTROY(false, null);


    private final boolean available;
    private final String lifecycleEvent;

    LifecycleState(boolean available, String lifecycleEvent) {
        this.available = available;
        this.lifecycleEvent = lifecycleEvent;
    }

    /**
     *
     * @return
     */
    public boolean isAvailable() {
        return available;
    }

    public String getLifecycleEvent() {
        return lifecycleEvent;
    }
}
