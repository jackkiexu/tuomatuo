package javax.servlet;

import java.util.Enumeration;

/**
 * A servlet configuration object used by a servlet container to pass
 * information to a servlet during initialization
 *
 * Created by xjk on 3/3/17.
 */
public interface ServletConfig {

    /**
     * Return the name of this servlet instance. The name may be provided via
     * server administration, assigned in the web application deployment
     * descriptor, or for an unregistered (and thus unnamed) servlet instance it
     * will be the servlet's class name.
     * @return
     */
    String getServletName();

    /**
     * Returns a reference to the {@link ServletContext} in which the caller is
     * executing
     *
     * @return a {@link ServletContext} object, used by the caller to interact
     *          with its servlet container
     */
    ServletContext getServletContext();

    /**
     * Returns a <code>String</code> containing the value of the named
     * initialization parameter, or <code>null</code> if the parameter does not
     * exist
     *
     * @param name
     * @return
     */
    String getInitParameter(String name);

    /**
     * Returns the names of the servlet's initialization parameters as an
     * <code>Enumeration</code> of <code>String</code> objects, or an empty
     * <code>Enumeration</code> if the servlet has no initialization parameters.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects
     *         containing the names of the servlet's initialization parameters
     */
    public Enumeration<String> getInitParameterNames();
}
