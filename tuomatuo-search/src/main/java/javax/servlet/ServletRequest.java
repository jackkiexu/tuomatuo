package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Defines an object to provide request information to a servlet. The
 * servlet container creates a <code>ServletRequest</code> object and passes it
 * as an argument to the servlet's <code>service</code> method
 *
 * <p>
 *   A <code>ServletRequest</code> object provides data including parameter name
 *   and values, attributes, and an input stream. Interfaces that extended
 *   <code>ServletRequest</code> can be provide additional protocol-specific data
 *   (for example, HTTP data is provided by HttpServletRequest)
 * </p>
 *
 * Created by xujiankang on 2017/3/3.
 */
public interface ServletRequest {

    /**
     * Returns the value of the named attribute as an <code>Object</code>, or
     * <code>null</code>, if no attribute of the given name exists
     *
     * <p>
     *      Attributes can be set two ways. The servlet container may set attributes
     *      to make available custom information about a request. For example, for
     *      requests made using HTTPS, the attribute
     *      Attributes can
     *      also be set programatically using {@link ServletRequest#setAttribute}
     *      This allows information to be embedded into a request before a
     *      {@link RequestDispatcher} call
     * </p>
     *
     * Attribute names should follow the same conventions as package names
     * Names beginning with <code>java.*</code> are
     * reserved for use by the Servlet specification. Names beginning with
     * <code>sun.*</code> are reserved for use by Oracle Corporation
     *
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * Returns and <code>Enumeration</code> containing the names of the
     * attributes available to this request. This method returns an empty
     * <code>Enumeration</code> if the request has no attributes available to
     * it
     * @return
     */
    Enumeration<String> getAttributeNames();

    /**
     * Returns the name of the character encoding used in the body of this
     * request. This method returns <code>null</code> if the request does not
     * specify a character encoding
     * @return
     */
    String getCharacterEncoding();

    /**
     * Overrides the name of the character encoding used in the body of this
     * request. This method must be called prior to reading request parameters
     * or reading input using getReader();
     * @param env
     * @throws UnsupportedEncodingException
     */
    void setCharacterEncoding(String env) throws UnsupportedEncodingException;

    /**
     * Returns the length, in bytes, of the request body and made available by
     * the input stream, or -1 if length is not known. For HTTP servlets
     * same as the value of the CGI vailable CONTENT_LENGTH
     * @return
     */
    int getContentLength();

    /**
     * Returns the length, in bytes, of the request body and made available by
     * the input stream, or -1 if the length is not known. For HTTP servlets,
     * same as the value of the CGI variable CONTENT_LENGTH.
     *
     * @return a long integer containing the length of the request body or -1 if
     *         the length is not known
     * @since Servlet 3.1
     */
    public long getContentLengthLong();

    /**
     * Returns MIME type of the body of the request, or <code>null</code> if
     * the type is not known. For HTTP servlets, same as the value of the CGI
     * variable CONTENT_TYPE
     * @return
     */
    String getContentType();

    /**
     * Retrieves the body of the request as binary data using a
     * {@link ServletInputStream}. Either this method {@link #getReader} may
     * be called to read the body, not both
     * @return
     * @throws IOException
     */
    ServletInputStream getInputStream() throws IOException;

    /**
     * Returns the value of a request parameter as a <code>String</code>, or
     * <code>null</code> if the parameter does not exist. Request parameters are
     * extra information sent with the request. For HTTP servlets, parameters
     * are contained in the query string or posted form data
     *
     * <p>
     *     You should only use this method when you are sure the parameter has only
     *     one value. If the parameter might have more than one value, use
     * </p>
     *
     * If you use this method with a multivalued parameter, the value returned
     * is equal to the first value in the array returned by
     * <code>getparameterValues</code>
     *
     * if the parameter data was sent in the request body, such as occurs with
     * an HTTP POST request, then reading the body directly via
     * {@link #getInputStream()} or {@link #getReader} can interfere with the execution of this method
     *
     * @param name
     * @return
     */
    String getParameter(String name);


    /**
     * Returns an <code>Enumeration</code> of String objects
     * containing the names of the parameters contained in this request. If the
     * request has no parameters, the method returns an empty
     *
     * @return
     */
    Enumeration<String> getParameterNames();

    /**
     * Returns an array String objects containing all of the values
     * the given request parameter has, or null if the parameter does not exist
     * @param name
     * @return
     */
    String[] getParameterValues(String name);

    /**
     * Returns a Map of the parameters of this request. Request parameters are extra information sent with
     * request. For HTTP servlets. parameters are contained in the query string or posted form
     * data
     * @return
     */
    Map<String, String[]> getParameterMap();

    /**
     * Return the name and version of the protocol the request uses in the form
     * <i>protocol/majorVersion.minorVersion</i> for example HTTP/1.1. For
     * HTTP servlets, the value returned is the same as the value of the CGI
     * variable
     * @return
     */
    String getProtocol();

    /**
     * Returns the name of the scheme used to make this request, for example,
     * HTTP, HTTPs, ftp
     * @return
     */
    String getScheme();

    /**
     * Returns the host name of the server to which  the request was sent, It is
     * the value of the part before ";" in the <code>Host</code> header values,
     * if any, or the resolved server name, or the server IP address
     * @return
     */
    String getServerName();

    /**
     *Returns the port number to which the request was sent. It is the value of
     * the part after ":" in the <code>Host</code> header value, if any, or the
     * server port where the client connection was accepted on
     * @return
     */
    int getServerPort();

    /**
     * Retrieves the body of the request as character data using a
     * BufferedReader The reader translates the character data
     * according to the character encoding used on the body. Either this method
     * or {@link #getInputStream()} may be called to read the body, not both
     * @return
     * @throws IOException
     */
    BufferedReader getReader() throws IOException;

    /**
     * Returns the IP address of the client or last proxy
     * that sent the request. For HTTP servlets, same as the value of the CGI
     * variable REMOTE_ADDR
     * @return
     */
    String getRemoteAddr();

    /**
     * Returns the fully qualified name of the client or the last proxy that
     * sent the request. If the engine cannot or chooses not to resolve the
     * hostname (to improve performance), This method returns the dotted-string
     * form of the IP address. For HTTP servlets, same as the value of the CGI
     * variable ReMOTE_HOST
     * @return
     */
    String getRemoteHost();

    /**
     * Stores an attribute in this request. Attribute are reset between
     * request. This method often is most used in conjunction with
     * {@ling RequestDispatcher}
     *
     * Attribute name should follow the same conventions as package names:
     * Names: begining with java.* reserved for use by the Servlet specification. names begining with
     * sun.* are reserved for use by Oracle
     *
     * if the object passed in is null, the effect is the same as calling
     * {@link #"removeAttribute}
     *
     * It is warned that when request is diapatched from the servlet resides
     * in a different web application by RequestDispatcher, the
     * object set by this method may not be correctly retrieved in the caller
     * servlet
     *
     * @param name
     * @param o
     */
    void setAttribute(String name, Object o);

    /**
     * Removes an attribute from this request. This method is not generally
     * needed as attributes only persist as long as the request is being
     * handled.
     * <p>
     * Attribute names should follow the same conventions as package names.
     * Names beginning with <code>java.*</code> and <code>javax.*</code> are
     * reserved for use by the Servlet specification. Names beginning with
     * <code>sun.*</code>, <code>com.sun.*</code>, <code>oracle.*</code> and
     * <code>com.oracle.*</code>) are reserved for use by Oracle Corporation.
     *
     * @param name
     *            a <code>String</code> specifying the name of the attribute to
     *            remove
     */
    public void removeAttribute(String name);

    /**
     * Returns the preferred Locale that the client will accept
     * content in, based on the Accept-Language header. If the client request
     * doesn't provide an Accept-language header. this method returns the
     * default locale for the server
     * @return
     */
    Locale getLocale();

    /**
     * Returns an <code>Enumeration</code> of <code>Locale</code> objects
     * indicating, in decreasing order starting with the preferred locale, the
     * locales that are acceptable to the client based on the Accept-Language
     * header. If the client request doesn't provide an Accept-Language header,
     * this method returns an <code>Enumeration</code> containing one
     * <code>Locale</code>, the default locale for the server.
     *
     * @return an <code>Enumeration</code> of preferred <code>Locale</code>
     *         objects for the client
     */
    public Enumeration<Locale> getLocales();

    /**
     * Returns a boolean indicating whether this request was made using a secure
     * channel, such as HTTPS.
     *
     * @return a boolean indicating if the request was made using a secure
     *         channel
     */
    public boolean isSecure();

    public RequestDispatcher getRequestDispatcher(String path);

    public String getRealPath(String path);

    public int getRemotePort();

    public String getLocalName();

    String getLocalAddr();

    int getLocalPort();

    ServletContext getServletContext();

    AsyncContext startAsync() throws IllegalStateException;

    AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException;

    boolean isAsyncStarted();

    boolean isAsyncSupported();

    /**
     * Get the current AsyncContext
     * @return
     */
    AsyncContext getAsyncContext();

    DispatcherType getDispatcherType();
}
