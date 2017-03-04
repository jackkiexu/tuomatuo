package javax.servlet;

import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

/**
 * Defines a set of methods that a servlet uses to communicate with its servlet
 * container, for example, to get the MIME type of a file, dispatch requests, or
 * write to a log file
 *
 * <p>
 *     There is one context per "web application" per Java Virtual Machine. (A
 *     "web application" is a collection of servlets and content installed under a
 *     specific subset of the server's URL namespace such as <code>catalog</code>)
 *     and possibly installed via a <code>.war</code> file
 * </p>
 *
 * In the case of a web application marked "distributed" in its deployment
 * descriptor, there will be one context instance for each virtual machine. In
 * this situation, the context cannot be used as a location to share global
 * information (because the information won't be truly global). Use an external
 * resource like a database instead
 *
 * The ServletContext object is contained within the
 * ServletConfig object, which the web server provides the servlet when
 * the servlet is initialized.
 *
 * Created by xjk on 3/3/17.
 */
public interface ServletContext {

    public static final String TEMPDIR = "java.servlet.context.tempdir";

    public static final String ORDERED_LIBS = "javax.servlet.context.orderedLibs";

    /**
     * Return the main path associated with this context
     * @return
     */
    String getContextPath();

    /**
     * Returns a <code>ServletContext</code> object that corresponds to a
     * specified URL on the server.
     *
     * This method allows servlets to gain access to the context for various
     * parts of the server, and as needed obtain {@link RequestDispatcher}
     * objects from the context. The given path must be begin with "/", is
     * interpreted relative to the server's document root and is matched against
     * the context roots of other web applications hosted on this container
     *
     * In a security conscious environment, the servlet container may return
     * null for a given URL
     *
     * @param uripath
     * @return
     */
    ServletContext getContext(String uripath);

    /**
     * Returns the major version of the Java Servlet API that this servlet
     * container supports. All implementations that comply with Version 3.1 must
     * have this method return the integer 3
     * @return
     */
    int getMajorVersion();

    /**
     * Returns the major version of the Java Servlet API that this servlet
     * container supports. All implementations that comply with Version 3.1 must
     * have this method return the integer 1
     * @return
     */
    int getMinorVersion();

    int getEffectiveMajorVersion();

    int getEffectiveMinorVersion();

    /**
     * Returns the MIME type of the specified file, or <code>null</code> if the
     * MIME type is not known. The MIME type is determined by the configuration
     * of the servlet container, and may be specified in a web application
     * deployment descriptor. Common MIME types are <code>"text/html"</code> and
     * <code>"image/gif"</code>
     *
     * @param file
     * @return
     */
    String getMimeType(String file);

    /**
     * Returns a directory-like listing of all the paths to resources within the
     * web application whose longest sub-path matches the supplied path
     * argument. Paths indicating subdirectory paths end with a '/'. The
     * returned paths are all relative to the root of the web application and
     * have a leading '/'. For example, for a web application containing<br>
     * <br>
     * /welcome.html<br>
     * /catalog/index.html<br>
     * /catalog/products.html<br>
     * /catalog/offers/books.html<br>
     * /catalog/offers/music.html<br>
     * /customer/login.jsp<br>
     * /WEB-INF/web.xml<br>
     * /WEB-INF/classes/com.acme.OrderServlet.class,<br>
     * <br>
     * getResourcePaths("/") returns {"/welcome.html", "/catalog/",
     * "/customer/", "/WEB-INF/"}<br>
     * getResourcePaths("/catalog/") returns {"/catalog/index.html",
     * "/catalog/products.html", "/catalog/offers/"}.<br>
     *
     * @param path
     *            the partial path used to match the resources, which must start
     *            with a /
     * @return a Set containing the directory listing, or null if there are no
     *         resources in the web application whose path begins with the
     *         supplied path.
     * @since Servlet 2.3
     */
    Set<String> getResourcePaths(String path);

    /**
     * Returns a URL to the resource that is mapped to a specified path. The
     * path must begin with a "/" and is interpreted as relative to the current
     * context root
     *
     * <p>
     *     This method allows the servlet container to make a resource available to
     *     servlets from any source. Resources can be located on a local or remote
     *     file system, in a database, or in a <code>.war</code> file
     * </p>
     *
     * The servlet container must implement the URL handlers and
     * URLConnection objects that are necessary to access the resource
     *
     * This method returns null if no resource is mapped to the
     * pathname
     * Some containers may allow writing to the URL returned by the this method
     * using the methods of the URL class
     *
     * The resource content is returned directly, so be aware that requesting a
     * .jsp page returns the JSP source code. Use a
     * RequestDispatcher instead to include results of an
     * execution
     * This method has a different purpose than
     * Class.getResource, which looks up resources based
     * on a class loader. This method does not use class loaders.
     *
     *
     * @param path
     * @return
     * @throws MalformedURLException
     */
    URL getResource(String path) throws MalformedURLException;

    /**
     * Returns the resource located at the named path as an
     * InputStream object
     *
     * The data in the InputStream can be of any type or length
     * The path must be specified according to the rules given in
     * getResource. This method returns null if no
     * resource exists at the specified path
     *
     * Meta-information such as content length type that is
     * available via getResource method is lost when using this
     * method
     *
     * The servlet container must implement the URL handlers and
     * URLConnection objects necessary to access the resource
     *
     * This method is different from
     * getResourceAsStream, which uses a class
     * loader. This method allows servlet containers to make a resource
     * available to a servlet from any location, without using a class
     * loader
     *
     * @param path
     * @return
     */
    InputStream getResourceAsStream(String path);


    /**
     * Returns a RequestDispatcher object that acts as a wrapper for the
     * resource located at the given path. A <code>RequestDispatcher</code>
     * Object can be used to forward a request to the resource or to include the
     * resource in a response. The resource can be dynamic or static.
     *
     * The pathname must begin with a "/" and is interpreted as relative to the
     * current context root. Use <code>getContext</code> to obtain a
     * method returns null if the ServletContext
     * canot return a RequestDispatcher
     *
     * @param path
     * @return
     */
    RequestDispatcher getRequestDispatcher(String path);

    /**
     * Returns a RequestDispatcher object that acts as a wrapper for the
     * named servlet
     * Servlets (and JSP pages also) may be given names via server
     * administration or via a web application deployment descriptor. A servlet
     * instance cane determine its name using
     * This method returns null if the ServletContext
     * cannot return a RequestDispatcher for any reason
     *
     * @param name
     * @return
     */
    RequestDispatcher getNameDispatcher(String name);

    /**
     * Do not use. This method was originally defined to retrieve a servlet from
     * a ServletContext. In this version, this method always
     * returns null and remains only to preserve binary
     * compatibility. This method will be permanently removed in a future
     * version of the Java Servlet API
     *
     * In lieu of this method, servlets can share information using the
     * ServletContext class and can perform shared business logic
     * by invoking methods on common non-servlet classes.
     *
     * @param name
     * @return
     * @throws ServletException
     */
    Servlet getServlet(String name) throws ServletException;

    /**
     * Do not use. This method was originally defined to return an
     * <code>Enumeration</code> of all the servlets known to this servlet
     * context. In this version, this method always returns an empty enumeration
     * and remains only to preserve binary compatibility. This method will be
     * permanently removed in a future version of the Java Servlet API.
     *
     * @return Always and empty Enumeration
     *
     * @deprecated As of Java Servlet API 2.0, with no replacement.
     */
    @SuppressWarnings("dep-ann")
    // Spec API does not use @Deprecated
    public Enumeration<Servlet> getServlets();

    /**
     * Do not use. This method was originally defined to return an
     * <code>Enumeration</code> of all the servlet names known to this context.
     * In this version, this method always returns an empty
     * <code>Enumeration</code> and remains only to preserve binary
     * compatibility. This method will be permanently removed in a future
     * version of the Java Servlet API.
     *
     * @return Always and empty Enumeration
     *
     * @deprecated As of Java Servlet API 2.1, with no replacement.
     */
    @SuppressWarnings("dep-ann")
    // Spec API does not use @Deprecated
    public Enumeration<String> getServletNames();

    /**
     * Writes the specified message to a servlet log file, usually an event log.
     * The name and type of the servlet log file is specific to the servlet
     * container.
     *
     * @param msg
     *            a <code>String</code> specifying the message to be written to
     *            the log file
     */
    public void log(String msg);

    /**
     * Do not use.
     * @param exception The exception to log
     * @param msg       The message to log with the exception
     * @deprecated As of Java Servlet API 2.1, use
     *             {@link #log(String message, Throwable throwable)} instead.
     *             <p>
     *             This method was originally defined to write an exception's
     *             stack trace and an explanatory error message to the servlet
     *             log file.
     */
    @SuppressWarnings("dep-ann")
    // Spec API does not use @Deprecated
    public void log(Exception exception, String msg);

    /**
     * Writes an explanatory message and a stack trace for a given
     * <code>Throwable</code> exception to the servlet log file. The name and
     * type of the servlet log file is specific to the servlet container,
     * usually an event log.
     *
     * @param message
     *            a <code>String</code> that describes the error or exception
     * @param throwable
     *            the <code>Throwable</code> error or exception
     */
    public void log(String message, Throwable throwable);

    /**
     * Returns a <code>String</code> containing the real path for a given
     * virtual path. For example, the path "/index.html" returns the absolute
     * file path on the server's filesystem would be served by a request for
     * "http://host/contextPath/index.html", where contextPath is the context
     * path of this ServletContext..
     * <p>
     * The real path returned will be in a form appropriate to the computer and
     * operating system on which the servlet container is running, including the
     * proper path separators. This method returns <code>null</code> if the
     * servlet container cannot translate the virtual path to a real path for
     * any reason (such as when the content is being made available from a
     * <code>.war</code> archive).
     *
     * @param path
     *            a <code>String</code> specifying a virtual path
     * @return a <code>String</code> specifying the real path, or null if the
     *         translation cannot be performed
     */
    public String getRealPath(String path);


    /**
     * Returns the name and version of the servlet container on which the
     * servlet is running.
     * <p>
     * The form of the returned string is
     * <i>servername</i>/<i>versionnumber</i>. For example, the JavaServer Web
     * Development Kit may return the string
     * <code>JavaServer Web Dev Kit/1.0</code>.
     * <p>
     * The servlet container may return other optional information after the
     * primary string in parentheses, for example,
     * <code>JavaServer Web Dev Kit/1.0 (JDK 1.1.6; Windows NT 4.0 x86)</code>.
     *
     * @return a <code>String</code> containing at least the servlet container
     *         name and version number
     */
    public String getServerInfo();

    /**
     * Returns a String containing the value of the named
     * context-wide initialization parameter, or null if the
     * parameter does not exist
     *
     * This method can make available configuration information useful to an
     * entire "web application". For example, it can provide a webmaster's email
     * address or the name of a system that holds critical data
     * @param name
     * @return
     */
    String getInitParameter(String name);

    /**
     * Return the names of the context's initialization parameters as an
     * Enumeration of String objects, or an empty
     * Enumeration if the context has no initialization parameters
     * @return
     */
    Enumeration<String> getInitParameterNames();

    /**
     * Set the givem initialisation parameter to the given value
     * @param name
     * @param value
     * @return
     */
    boolean setInitParameter(String name, String value);

    /**
     * Returns the servlet container attribute with the given name, or
     * null if there is no attribute by the name. An attribute
     * allows a servlet container to given the servlet additional information not
     * already provided by this interface. See your server documentation for
     * information about its attributes. A list of supported attributes can be
     * retrieved using getAttributeNames
     *
     * The attribute is returned as a Object or some
     * subclass. Attribute name should follow the same convention as package
     * names. The Java Servlet API specification reserves names matching
     *
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * Returns an <code>Enumeration</code> containing the attribute names
     * available within this servlet context. Use the {@link #getAttribute}
     * method with an attribute name to get the value of an attribute.
     *
     * @return an <code>Enumeration</code> of attribute names
     * @see #getAttribute
     */
    public Enumeration<String> getAttributeNames();

    /**
     * Binds an object to a given attribute name in this servlet context. If the
     * name specified is already used for an attribute. this method will replace
     * the attribute with the new to the new attribute
     * If listener are configured on the ServletContext the
     * container notifies them accordingly
     * If a null value is passed, the effect is the same as calling
     * removeAttributes
     * Attribute names should follow the same convention as package name
     *
     * @param name
     * @param object
     */
    void setAttribute(String name, Object object);

    /**
     * Removes the attribute with the given name from the servlet context. After
     * removal, subsequent calls to {@link #getAttribute(String)} to retrieve the
     * attribute's value will return null
     * If listener are configured on the <code>ServletContext</code> the
     * container notifies them accordingly
     * @param name
     */
    void removeAttribute(String name);

    /**
     * Returns the name of this web application corresponding to this
     * ServletContext as specified in the deployment descriptor for this web
     * application by the display-name element
     *
     * @return
     */
    String getServletContextName();

    /**
     * Register a servlet implementation for use in this ServletContext.
     * @param servletName The name of the servlet to register
     * @param className   The implementation class for the servlet
     * @return The registration object that enables further configuration
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(String servletName, String className);


    /**
     * Register a servlet instance for use in this ServletContext.
     * @param servletName The name of the servlet to register
     * @param servlet     The Servlet instance to register
     * @return The registration object that enables further configuration
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet);


    /**
     * Add servlet to context.
     * @param   servletName  Name of servlet to add
     * @param   servletClass Class of servlet to add
     * @return  <code>null</code> if the servlet has already been fully defined,
     *          else a {@link javax.servlet.ServletRegistration.Dynamic} object
     *          that can be used to further configure the servlet
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public ServletRegistration.Dynamic addServlet(String servletName,
                                                  Class<? extends Servlet> servletClass);

    /**
     * TODO SERVLET3 - Add comments
     * @param <T> TODO
     * @param c   TODO
     * @return TODO
     * @throws ServletException TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public <T extends Servlet> T createServlet(Class<T> c)
            throws ServletException;


    /**
     * Obtain the details of the named servlet.
     *
     * @param servletName   The name of the Servlet of interest
     *
     * @return  The registration details for the named Servlet or
     *          <code>null</code> if no Servlet has been registered with the
     *          given name
     *
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     *
     * @since Servlet 3.0
     */
    public ServletRegistration getServletRegistration(String servletName);


    /**
     * TODO SERVLET3 - Add comments
     * @return TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public Map<String, ? extends ServletRegistration> getServletRegistrations();


    /**
     * Add filter to context.
     * @param   filterName  Name of filter to add
     * @param   className Name of filter class
     * @return  <code>null</code> if the filter has already been fully defined,
     *          else a {@link javax.servlet.FilterRegistration.Dynamic} object
     *          that can be used to further configure the filter
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(String filterName, String className);

    /**
     * Add filter to context.
     * @param   filterName  Name of filter to add
     * @param   filter      Filter to add
     * @return  <code>null</code> if the filter has already been fully defined,
     *          else a {@link javax.servlet.FilterRegistration.Dynamic} object
     *          that can be used to further configure the filter
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter);

    /**
     * Add filter to context.
     * @param   filterName  Name of filter to add
     * @param   filterClass Class of filter to add
     * @return  <code>null</code> if the filter has already been fully defined,
     *          else a {@link javax.servlet.FilterRegistration.Dynamic} object
     *          that can be used to further configure the filter
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws IllegalStateException
     *             If the context has already been initialised
     * @since Servlet 3.0
     */
    public FilterRegistration.Dynamic addFilter(String filterName,
                                                Class<? extends Filter> filterClass);

    /**
     * TODO SERVLET3 - Add comments
     * @param <T> TODO
     * @param c   TODO
     * @return TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws ServletException TODO
     * @since Servlet 3.
     */
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException;

    /**
     * TODO SERVLET3 - Add comments
     * @param filterName TODO
     * @return TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public FilterRegistration getFilterRegistration(String filterName);

    /**
     * @return TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0 TODO SERVLET3 - Add comments
     */
    public Map<String, ? extends FilterRegistration> getFilterRegistrations();

    SessionCookieConfig getSessionCookieConfig();

    void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes);

    /**
     * Obtain the default session tracking modes for this web application
     * By default SessionTrackingMode is always supported,
     * SessionTrackingMode#COOKIE is supported unless the cookies
     * attribute has been set to false for the context and
     * SessionTrackingMode#SSL is supported if at least one of the connectors
     * used by this context has the attribute secure set to
     * true
     * @return
     */
    Set<SessionTrackingMode> getDefaultSessionTrackingModes();

    /**
     * Obtains the currently enabled session tracking modes for this web
     * application.
     * @return The value supplied via {@link #setSessionTrackingModes(Set)} if
     *         one was previously set, else return the defaults
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes();

    /**
     * TODO SERVLET3 - Add comments
     * @param className TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public void addListener(String className);

    /**
     * TODO SERVLET3 - Add comments
     * @param <T> TODO
     * @param t   TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public <T extends EventListener> void addListener(T t);

    /**
     * TODO SERVLET3 - Add comments
     * @param listenerClass TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public void addListener(Class<? extends EventListener> listenerClass);

    /**
     * TODO SERVLET3 - Add comments
     * @param <T> TODO
     * @param c TODO
     * @return TODO
     * @throws ServletException TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0
     */
    public <T extends EventListener> T createListener(Class<T> c)
            throws ServletException;

    /**
     * @return TODO
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @since Servlet 3.0 TODO SERVLET3 - Add comments
     */
    public JspConfigDescriptor getJspConfigDescriptor();

    /**
     * Get the web application class loader associated with this ServletContext.
     *
     * @return The associated web application class loader
     *
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws SecurityException if access to the class loader is prevented by a
     *         SecurityManager
     * @since Servlet 3.0
     */
    public ClassLoader getClassLoader();

    /**
     * Add to the declared roles for this ServletContext.
     * @param roleNames The roles to add
     * @throws UnsupportedOperationException    If called from a
     *    {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *    method of a {@link ServletContextListener} that was not defined in a
     *    web.xml file, a web-fragment.xml file nor annotated with
     *    {@link javax.servlet.annotation.WebListener}. For example, a
     *    {@link ServletContextListener} defined in a TLD would not be able to
     *    use this method.
     * @throws IllegalArgumentException If the list of roleNames is null or
     *         empty
     * @throws IllegalStateException If the ServletContext has already been
     *         initialised
     * @since Servlet 3.0
     */
    public void declareRoles(String... roleNames);

    /**
     * Get the primary name of the virtual host on which this context is
     * deploy. The name may or may not be a valid host name
     *
     * @return
     */
    String getVirtualServerName();





}
