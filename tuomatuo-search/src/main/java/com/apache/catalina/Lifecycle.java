package com.apache.catalina;

/**
 *
 * Created by xujiankang on 2017/2/27.
 */
public interface Lifecycle {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The LifecycleEvent type for the "component before init" event.
     */
    public static final String BEFORE_INIT_EVENT = "before_init";


    /**
     * The LifecycleEvent type for the "component after init" event.
     */
    public static final String AFTER_INIT_EVENT = "after_init";


    /**
     * The LifecycleEvent type for the "component start" event.
     */
    public static final String START_EVENT = "start";


    /**
     * The LifecycleEvent type for the "component before start" event.
     */
    public static final String BEFORE_START_EVENT = "before_start";


    /**
     * The LifecycleEvent type for the "component after start" event.
     */
    public static final String AFTER_START_EVENT = "after_start";


    /**
     * The LifecycleEvent type for the "component stop" event.
     */
    public static final String STOP_EVENT = "stop";


    /**
     * The LifecycleEvent type for the "component before stop" event.
     */
    public static final String BEFORE_STOP_EVENT = "before_stop";


    /**
     * The LifecycleEvent type for the "component after stop" event.
     */
    public static final String AFTER_STOP_EVENT = "after_stop";


    /**
     * The LifecycleEvent type for the "component after destroy" event.
     */
    public static final String AFTER_DESTROY_EVENT = "after_destroy";


    /**
     * The LifecycleEvent type for the "component before destroy" event.
     */
    public static final String BEFORE_DESTROY_EVENT = "before_destroy";


    /**
     * The LifecycleEvent type for the "periodic" event.
     */
    public static final String PERIODIC_EVENT = "periodic";


    /**
     * The LifecycleEvent type for the "configure_start" event. Used by those
     * components that use a separate component to perform configuration and
     * need to signal when configuration should be performed - usually after
     * {@link #BEFORE_START_EVENT} and before {@link #START_EVENT}.
     */
    public static final String CONFIGURE_START_EVENT = "configure_start";


    /**
     * The LifecycleEvent type for the "configure_stop" event. Used by those
     * components that use a separate component to perform configuration and
     * need to signal when de-configuration should be performed - usually after
     * {@link #STOP_EVENT} and before {@link #AFTER_STOP_EVENT}.
     */
    public static final String CONFIGURE_STOP_EVENT = "configure_stop";


    /**
     * Add a LifecycleEvent listener to this component
     * @param listener
     */
    void addLifecycleListener(LifecycleListener listener);

    /**
     * Get the life cycle listeners associated with this life cycle
     * @return
     */
    LifecycleListener[] findLifecycleListeners();

    /**
     * Remove a LifecycleEvent listener from this component;
     * @param listener
     */
    void removeLifecycleListener(LifecycleListener listener);

    /**
     * Pepare the component for staring. This method should perform any
     * initialization required post object creation. The following
     * {@link LifecycleEvent} s will be fired in the following order
     * <ol>
     *     <li>
     *         INIT_EVENT: On the successful completion of component
     *         initialization
     *     </li>
     * </ol>
     * @throws LifecycleException
     */
    void init() throws LifecycleException;


    /**
     * Prepare for the beginning of active use of the public methods other than
     * property getters/setters and life cycle methods of this component. This
     * method should be called before any of the public methods other than
     * property getters/setters and life cycle methods of this component are
     * utilized. The following {@link LifecycleEvent } will be fired in the
     * following order:
     *
     * Before_START_EVENT : At the begining of the method. It is as this
     *          pont the state transitions to
     *
     *
     *
     * @throws LifecycleException
     */
    void start() throws LifecycleException;

    /**
     * Gracefully terminate the active use of the public methods other thab
     * property getters/setters and life cycle methods of this component. Once
     * the STOP_EVENT if fired, the public methods other than property
     * getters/setters and life cycle methods should not be used. The following
     * {@link LifecycleException} will be fired in the following ordering:
     *
     * BEFORE_STOP_EVENT: At the beginning of the method. It is at this
     *                  point that the state transitions to {@link LifecycleState#STOPPING_PREP}
     *
     * STOP_EVENT : During the method once it is safe to call stop() for
     *              any child components. It is safe to call stop() for
     *
     *
     *
     * @throws LifecycleException
     */
    void stop() throws LifecycleException;

    /**
     * Prepare to discard the object. The following {@link LifecycleEvent}s will
     * be fired in the following order:
     *
     * DESTROY_EVENT: On the successful completion of component
     *                  destruction
     *
     * @throws LifecycleException
     */
    public void destroy() throws LifecycleException;

    /**
     * Obtain the current state of the source component
     * @return The current state of the source component
     */
    public LifecycleState getState();


    /**
     * Obtain a textual representation of the current component state.  Usefule
     * for JMX. The format of this string may vary between point releases and
     * should not be relied upon determine component state To determine
     * component state, use {@link #getState()}
     * @return
     */
    String getStateName();


    /**
     * Marker interface used to indicate that the instance should only be used
     * once. Calling {@link #stop()} on an instance that supports this interface
     * will automatically call {@link #destory} after {@link #stop}
     */
    interface SingleUse{

    }
}
