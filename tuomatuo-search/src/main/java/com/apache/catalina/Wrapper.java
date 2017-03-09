package com.apache.catalina;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

/**
 * A <b>Wrapper</b> is a Container that represents an individual servlet
 * definition from the deployment descriptor of the web application. It
 * provides a convenient mechanism to use Interceptors that see every single
 * request to the servlet represented by this definition
 * <p>
 *     Implementations of Wrapper are responsible for managing the servlet life
 *     cycle for their underlying servlet class, including calling init() and
 *     destroy() at appropriate times, as well as respecting the existence of
 *     the SingleThreadModel declaration on the servlet class itself
 * </p>
 *
 * The parent Container attached to a Wrapper will generally be an
 * implementation of Context, representing the servlet context (and
 * therefore the web application) within which this servlet executes
 * Child Container are not allowed on Wrapper implementation, so the
 * addChild() method should throw an IllegalArgumentException
 *
 * Created by xjk on 3/6/17.
 */
public interface Wrapper extends Container {

    /**
     * Container event for adding a wrapper
     */
    static final String ADD_MAPPING_EVENT = "addMapping";

    static final String REMOVE_MAPPING_EVENT = "removeMapping";

    /**
     * Return the available date/time for this servlet, in milliseconds since
     * the epoch. If this date/time is in the future, any request for this
     * servlet will return as SC_SERVICE_UNAVAILABLE error, If it is zero,
     * the servlet is currently available. A value equal to Long.MAX_VALUE
     * is considered to mean the unavailablility is permanent
     * @return
     */
    long getAvailable();

    /**
     * Set the available date/time for this servlet, in milliseconds since the
     * epoch. If this date/time is in the future, any request for this servlet
     * will return ac SC_SERVICE_UNAVAILABLE error. A value equal to
     * Long.MAX_VALUE is considered to mean that unavailability is permenent
     * @param available
     */
    void setAvailable(long available);

    /**
     * Return the load-on-startup order value (negative value means
     * load on first call)
     * @return
     */
    int getLoadOnStartup();

    /**
     * Set the load-on-startup order value (negative value means
     * load on first call)
     * @param value
     */
    void setLoadOnStartup(int value);

    /**
     * Return the run-as identify for this servlet
     * @return
     */
    String getRunAs();

    /**
     * Set the run-as identity for this servlet
     * @param runAs
     */
    void setRunAs(String runAs);

    /**
     * Return the fully qualified servlet class name for this servlet
     * @return
     */
    String getServletClass();

    /**
     * Set the fully qualified servlet class name for this servlet
     *
     * @param servletClass
     */
    void setServletClass(String servletClass);

    /**
     * Gets the names of the methods supported by the underlying servlet
     *
     * This is the same set of methods included in the Allow response header
     * in response to an OPTIONS request method processed by the underlying
     * servlet
     *
     * @return Array of names of the methods supported by the underlying
     *          servlet
     * @throws ServletException
     */
    String[] getServletMethods() throws ServletException;

    /**
     * Is this servlet currently unavailable
     * @return
     */
    boolean siUnavailable();

    /**
     * Return the assiciated servlet instance
     * @return
     */
    Servlet getServlet();

    /**
     * Set the associated servlet name
     * @param servlet
     */
    void setServlet(Servlet servlet);

    /**
     * Add a new servlet initialization parameter for this servlet
     * @param name Name of this initialization parameter to add
     * @param value Value of this initialization parameter to add
     */
    void addInitParameter(String name, String value);

    /**
     * Add a mapping associated with the Wrapper
     * @param mapping
     */
    void addMapping(String mapping);

    /**
     * Add a new security role reference record to the set of records for
     * this servlet
     *
     * @param name Role name used within this servlet
     * @param link Role name used within the web application
     */
    void addSecurityReference(String name, String link);

    /**
     * Allocate an initialized instance of this Servlet that is ready to have
     * its <code>service()</code> method called. If the servlet class does
     * not implement <code>SingleThreadModel</code>, the (only) initialized
     * instance may be returned immediately. If the servlet class implements
     * <code>SingleThreadModel</code>, the wrapper implementation must ensure
     * that this instance is not allocated again until it is deallocated by a
     * call to <code>deallocate()</code>
     * @return
     * @throws ServletException if the servlet init() method threw an Exception
     */
    Servlet allocate() throws ServletException;

    /**
     * Return this previously allocated servlet to the pool of available
     * instances. If this servlet class does not implement SingleThreadModel,
     * no action is actually required
     * @param servlet The servlet to be returned
     * @throws ServletException
     */
    void deallocate(Servlet servlet) throws ServletException;

    /**
     * Return the value for the specified initialization parameter name,
     * if any; otherwise return <code>null</code>
     * @param name
     * @return
     */
    String findInitParameter(String name);

    /**
     * Return the names of all defined initialization parameters for this
     * servlet
     * @return
     */
    String[] findInitParameters();

    /**
     * Return the mapping associated with this wrapper
     * @return
     */
    String[] findMappings();

    /**
     * Return the security role link for the specified security role
     * reference name, if any; otherwise return <code>null</code> .
     * @param name Security role reference used within this servlet
     * @return
     */
    String findSecurityReference(String name);

    /**
     * Returns the set of security role reference names associated with
     * this servlet, if any; otherwise return a zero-length array
     * @return
     */
    String[] findSecurityReference();

    /**
     * Increment the error count value used when monitoring
     */
    void incrementErrorCount();

    /**
     * Load and initialize an instance of this servlet, if there is not already
     * at least one initialized instance. This can be used, for example, to
     * load servlets that are marked in the deploymet descriptor to be loaded
     * at server startup time
     * @throws ServletException
     */
    void load() throws ServletException;

    /**
     * Remove the specified initialization parameter from this servlet
     * @param name Name of the initialization parameter to remove
     */
    void removeInitParameter(String name);

    /**
     * Remove a mapping assocaited with the wrapper
     * @param mapping
     */
    void removeMapping(String mapping);

    /**
     * Remove an security reference for the specified role name
     * @param name
     */
    void removeSecurityReference(String name);

    /**
     * Process an UnavailableException, marking this servlet as unavailable
     * for the specified amount of time
     * @param unavailable
     */
    void unavailable(UnavailableException unavailable);

    /**
     * Unload all initialized instance of this servlet, after calling the
     * <code>destroy()</code> method for each instance. This can be used,
     * for example, prior to shutting down entire servlet engine, or
     * prior to reloading all of the classes from the Loader associated with
     * our Loader's repository
     *
     * @throws ServletException
     */
    void unload() throws ServletException;

    /**
     * Get the multi-part configuration for the associated servlet. If no
     * multi-part configuration has been defined, then <code>null</code> will be
     * returned
     * @return
     */
    MultipartConfigElement getMultipartConfigElement();

    /**
     * Set the multi-part configuration for the associated servlet. To clear the
     * multi-part configuration specify <code>null</code> as the new value
     * @param multipartConfig
     */
    void setMultipartConfigElement(MultipartConfigElement multipartConfig);

    /**
     * Does the associated Servlet support async processing? Default to
     * <code>false</code>
     * @return
     */
    boolean isAsyncSupported();

    /**
     * Set the async support for the associated servlet
     */
    void setAsyncSupported(boolean asyncSupport);

    /**
     * Is the associated Servlet enabled? Default to <code>true</code>
     */
    boolean isEnabled();

    /**
     * Set the flag that indicates
     * {@link javax.servlet.annotation.ServletSecurity} annotations must be
     * scanned when the Servlet is first used.
     *
     * @param b The new value of the flag
     */
    public void setServletSecurityAnnotationScanRequired(boolean b);

    /**
     * Sets the enabled attribute for the associated servlet
     * @param enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Scan for (if necessary) and process (if found) the
     * {@link javax.servlet.annotation.ServletSecurity} annotation for the
     * Servlet associated with this wrapper
     * @throws ServletException
     */
    void servletSecurityAnnotationScan() throws ServletException;

    /**
     * Is the Servlet overridable by a ServletContainerInitializer
     * @return
     */
    boolean isOverridable();

    /**
     * Sets the overridable attribute for this Servlet
     * @param overridable
     */
    void setOverridable(boolean overridable);
}
