package javax.servlet.http;

import javax.servlet.ServletContext;
import java.util.Enumeration;


/**
 * Provides a way to identify a user across more than one page request or visit
 * to a Web site and to store information about that user.
 * <p>
 * The servlet container uses this interface to create a session between an HTTP
 * client and an HTTP server. The session persists for a specified time period,
 * across more than one connection or page request from the user. A session
 * usually corresponds to one user, who may visit a site many times. The server
 * can maintain a session in many ways such as using cookies or rewriting URLs.
 * <p>
 * This interface allows servlets to
 * <ul>
 * <li>View and manipulate information about a session, such as the session
 * identifier, creation time, and last accessed time
 * <li>Bind objects to sessions, allowing user information to persist across
 * multiple user connections
 * </ul>
 * <p>
 * When an application stores an object in or removes an object from a session,
 * the session checks whether the object implements
 * {@link HttpSessionBindingListener}. If it does, the servlet notifies the
 * object that it has been bound to or unbound from the session. Notifications
 * are sent after the binding methods complete. For session that are invalidated
 * or expire, notifications are sent after the session has been invalidated or
 * expired.
 * <p>
 * When container migrates a session between VMs in a distributed container
 * setting, all session attributes implementing the
 * {@link HttpSessionActivationListener} interface are notified.
 * <p>
 * A servlet should be able to handle cases in which the client does not choose
 * to join a session, such as when cookies are intentionally turned off. Until
 * the client joins the session, <code>isNew</code> returns <code>true</code>.
 * If the client chooses not to join the session, <code>getSession</code> will
 * return a different session on each request, and <code>isNew</code> will
 * always return <code>true</code>.
 * <p>
 * Session information is scoped only to the current web application (
 * <code>ServletContext</code>), so information stored in one context will not
 * be directly visible in another.
 *
 * @see HttpSessionBindingListener
 */
public interface HttpSession {

    /**
     * Returns the time when this session was created, measured in milliseconds
     * since midnight January 1, 1970 GMT
     * @return
     */
    long getCreationTime();

    /**
     * Returns a stringcontaining the unique identifier assigned to this
     * session. The identifier is assigned by the servlet container and is
     * implementation dependent
     *
     * @return a string specifying the identifier assigned to this session
     */
    String getId();

    /**
     * Returns the last time the client sent a request associated with this
     * session, as the number of milliseconds since midnight January 1, 1970
     * CMT, and marked by the time the container received the request
     *
     * Actions that your application takes, such as getting or setting a value
     * associated with the session, do not affect the access time.
     *
     * @return a <code>long</code> representing the last time the client sent a
     *          request associated with this session, expressed in milliseconds
     *          since 1/1/1970 GMT
     */
    long getLastAccessedTime();

    /**
     * Returns the ServletContext to which this session belongs
     * @return
     */
    ServletContext getServletContext();

    /**
     * Specifies the time, in seconds, between client requests before the
     * servlet container will invalidate this session, A zero or negative time
     * indicates that the session should never timeout
     * @param interval
     */
    void setMaxInactiveInterval(int interval);

    /**
     * Returns the maximum time interval, in seconds, that the servlet container
     * will keep this session open between client access. After this interval,
     * the servlet container will invalidate the session. The maximum time
     * interval can be set with the <code>setMaxInactiveInterval</code> method
     * A zero or negative time indicates that the session should never timeout
     * @return
     */
    int getMaxInactiveInternal();

    HttpSessionContext getSessionContext();

    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name
     *            a string specifying the name of the object
     * @return the object with the specified name
     * @exception IllegalStateException
     *                if this method is called on an invalidated session
     */
    Object getAttribute(String name);

    Object getValue(String name);

    /**
     * Returns an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of all the objects bound to this session.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects
     *         specifying the names of all the objects bound to this session
     * @exception IllegalStateException
     *                if this method is called on an invalidated session
     */
    Enumeration<String> getAttributeNames();

    /**
     *
     * @return an array of <code>String</code> objects specifying the names of
     *          all the objects bound to this session
     */
    String[] getValueNames();

    /**
     * Binds an object to this session, using the name specified. If an object
     * of the sameis already bound to the session, the object is replaced
     * <p>
     * After this method executes, and if the new object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.
     * <p>
     * If an object was already bound to this session of this name that
     * implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     * <p>
     * If the value passed in is null, this has the same effect as calling
     * <code>removeAttribute()</code>.
     *
     * @param name
     * @param value
     */
    void setAttribute(String name, Object value);

    void putValue(String name, Object value);

    /**
     * Removes the object bound with the specified name from this session. If
     * the session does not have an object bound with the specified name, this
     * method does nothing
     * <p>
     *     After this method executes, and if the object implements
     *     <code>HttpSessionBindListener</code>, the container calls
     *     <code>HttpSessionBindListener#valueUnbound</code>. The container then
     *     notifies any <code>HttpSessionAttributeListener</code>s in the web
     *     application
     * </p>
     * @param name
     */
    void removeAttribute(String name);


    void removeValue(String name);

    /**
     * Invalidates this session then unbinds any objects bound to it
     */
    void invalidate();

    /**
     * Returns <code>true</code> if the client not yet know about the
     * session or if the client choose not to join the session. For example, if
     * the server used only cookie-based sessions, and the client hah disabled
     * the use of cookie, then a session would be new on each request
     *
     * @return <code>true</code> if the server has created a session, but the
     *          client has not yet joined
     */
    boolean isNew();

}
