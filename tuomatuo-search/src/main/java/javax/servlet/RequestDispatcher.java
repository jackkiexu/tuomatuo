package javax.servlet;

import java.io.IOException;

/**
 * Define an object that receives requests from the client and sends them to
 * any resource (such as a servlet, HTML file, or JSP fle) on the server. The
 * servlet container creates the <code>RequestDispatcher</code> object, which is
 * used as a wrapper around a server resource located at a particular path or
 * given by a particular name.
 *
 * <p>
 *     This interface is intended to wrap servlets, but a servlet container can
 *     create <code>RequestDispatcher</code> objects to wrap any type of resource
 * </p>
 *
 * Created by xjk on 3/4/17.
 */
public interface RequestDispatcher {

    /**
     * The name of the request attribute that should be set by te container
     * when the forward method is
     * called. It provides the original value of a path-related property of the
     * request. See the chapter "Forwarded Request Parameters " in the Servlet
     * Specification for details
     */
    static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";

    /**
     * The name of the request attribute that should be set by the container
     * when the forward method is
     * called. It provides the original value of a path-related property of the
     * request
     */
    static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";

    /**
     * The name of the request attribute that should be set by the container
     * when the forward method is
     * called. It provides the original value of a path-related property of the
     * request
     */
    static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";

    /**
     * The name of the request attribute that should be set by the container
     * when the forward methid
     * called
     */
    static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";

    static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";

    static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    static final String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";

    static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";

    static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";

    static final String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";

    static final String ERROR_EXCEPTION = "javax.servlet.error.exception";

    static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";

    static final String ERROR_MESSAGE = "javax.servlet.error.message";

    static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

    static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";

    static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";


    /**
     * Forwards a request from a servlet to another resource (servlet, JSP file,
     * or HTML file) on the server. This method allows one servlet to do
     * preliminary processing of a request and another resource to generate the
     * response.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void foward(ServletRequest request, ServletResponse response) throws ServletException, IOException;

    /**
     * Includes the content of a resource (servlet, JSP page, HTML file) in the
     * response. In essence, this method enables programmatic server-side
     * includes
     *
     * The ServletResponse object has its path elements and parameters
     * remain unchanged from the caller's. The included servlet connot change
     * the response status code or set headers; any attempt to make a change is
     * ignored
     *
     * The request and response parameters must be either the same objects as
     * were passed to the calling servlet's service method or be subclasses of
     * the {@link ServletRequestWrapper} or {@link ServletResponseWrapper}
     * classes that wrap them
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void include(ServletRequest request, ServletResponse response) throws ServletException, IOException;
}
