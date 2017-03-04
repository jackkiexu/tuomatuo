package javax.servlet;

import java.util.Enumeration;

/**
 * A filter configuration object used by a servlet container to pass information
 * to a filter during initialization
 *
 * Created by xjk on 3/4/17.
 */
public interface FilterConfig {

    /**
     * Get the name of the filter
     * @return
     */
    String getFilterName();

    /**
     * Returns a reference to the {@link ServletContext} in which the caller is
     * executing
     *
     * @return {@link ServletContext} object, used by the caller to interact
     *              with its servlet container
     */
    ServletContext getServletContext();


    /**
     * Returns a <code>String</code> containing the value of the named
     * initialization parameter, or <code>null</code> if the parameter does not
     * exist
     *
     * @param name
     * @return <code>String</code> containg the value of the initialization
     *          parameter
     */
    String getInitParameter(String name);

    /**
     * Returns the names of the filter's initialization parameters as an
     * <code>Enumeration</code> of <code>String</code> objects, or an empty
     * <code>Enumeration</code> if the filter has no initialization parameters.
     *
     * @return <code>Enumeration</code> of <code>String</code> objects
     *         containing the names of the filter's initialization parameters
     */
    public Enumeration<String> getInitParameterNames();
}
