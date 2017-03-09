package com.apache.catalina;

/**
 *
 * <p>
 *     Interface describling a collecton of Valves that should be executed
 *     in sequence when the <code>invoke()</code> method is invoke. It is
 *     required that a Valve some where in the pipeline (usually the last one )
 *     must process the request and create the corresponding response, rather
 *     than trying to pass the request on
 * </p>
 *
 * There is generally a single instance associated with each
 * Container. The Container's normal request processing functionality is
 * generally encapsulated in a contaner-specific Valve. which should always
 * be executed at the end of a pipeline. To facilitate this, the
 * <code>setBasic</code> method is provided to set the Valve instance that
 * will always be executed last. Other Valves will be executed in the order
 * that they were added. before the basic Valve is executed
 *
 * Created by xjk on 3/6/17.
 */
public interface Pipeline {

    /**
     * the Valve instance that has been distinguished as the basic
     * Valve for this Pipeline
     * @return
     */
    Valve getBasic();

    /**
     *  Set the Valve instance that has been distinguished as the basic
     *  Valve for this Pipeline (if any). Prior to setting the basic Valve
     *  the Valve's <code>setContainer</code>
     * @param valve
     */
    void setBasic(Valve valve);

    /**
     * Add a new Valve to the end of the pipeline associated with this
     * Container. Prior to adding the Valve, the Valve's
     * <code>setContainer</code> method will be called. if it implements
     * <code>Contained</code> with the owning Container as an argument
     * The method may throw an IllegalArgumentException if this Valve chooses not to
     * be associated with this Container, or <code>IllegalStateException</code>
     * if it is already associated with a different Container
     * @param valve
     */
    void addValve(Valve valve);

    /**
     * the set of Valves in the pipeline associated with this
     * Container, including the basic Valve(if any). If there are no
     * such Valves, a zero-length array is returned
     * @return
     */
    Valve[] getValve();


    /**
     * Remove the specified Valve from the pipeline associated with this
     * Container, if it is found; otherwise do nothing. If the Valve is
     * found and removed. the Valve's <code>setContainer</code> method
     * will be called if it implements Contained
     * @param valve
     */
    void removeValve(Valve valve);

    /**
     * The Valve instance that has been distinguished as the basic
     * Valve for this Pipeline
     *
     * @return
     */
    Valve getFirst();
    /**
     * Returns true if all the valves in this pipeline support async, false otherwise
     * @return
     */
    boolean isAsyncSupported();

    /**
     * the container with which this Pipeline is associated
     * @return
     */
    Container getContainer();

    /**
     * Set the Container with which this Pipeline is associated
     * @param container
     */
    void setContainer(Container container);

}
