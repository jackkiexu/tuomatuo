package com.apache.catalina;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Iterator;

/**
 * A session is the Catalina-interval facade for an
 * HttpSession that is used to maintain state information
 * between requests for a particular user of a web application
 *
 * Created by xjk on 3/6/17.
 */
public interface Session {



    // ----------------------------------------------------- Manifest Constants


    /**
     * The SessionEvent event type when a session is created.
     */
    public static final String SESSION_CREATED_EVENT = "createSession";


    /**
     * The SessionEvent event type when a session is destroyed.
     */
    public static final String SESSION_DESTROYED_EVENT = "destroySession";


    /**
     * The SessionEvent event type when a session is activated.
     */
    public static final String SESSION_ACTIVATED_EVENT = "activateSession";



    // ------------------------------------------------------------- Properties


    /**
     * Return the authentication type used to authenticate our cached
     * Principal, if any.
     */
    public String getAuthType();


    /**
     * Set the authentication type used to authenticate our cached
     * Principal, if any.
     *
     * @param authType The new cached authentication type
     */
    public void setAuthType(String authType);


    /**
     * Return the creation time for this session.
     */
    public long getCreationTime();


    /**
     * Return the creation time for this session, bypassing the session validity
     * checks.
     */
    public long getCreationTimeInternal();


    /**
     * Set the creation time for this session.  This method is called by the
     * Manager when an existing Session instance is reused.
     *
     * @param time The new creation time
     */
    public void setCreationTime(long time);


    /**
     * Return the session identifier for this session.
     */
    public String getId();


    /**
     * Return the session identifier for this session.
     */
    public String getIdInternal();


    /**
     * Set the session identifier for this session and notifies any associated
     * listeners that a new session has been created.
     *
     * @param id The new session identifier
     */
    public void setId(String id);


    /**
     * Set the session identifier for this session and optionally notifies any
     * associated listeners that a new session has been created.
     *
     * @param id        The new session identifier
     * @param notify    Should any associated listeners be notified that a new
     *                      session has been created?
     */
    public void setId(String id, boolean notify);

    /**
     * Return the last time the client sent a request associated with this
     * session, as the number of milliseconds since midnight, January 1, 1970
     * GMT.  Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access time.
     * This one gets updated whenever a request starts.
     */
    public long getThisAccessedTime();

    /**
     * Return the last client access time without invalidation check
     * @see #getThisAccessedTime()
     */
    public long getThisAccessedTimeInternal();

    /**
     * Return the last time the client sent a request associated with this
     * session, as the number of milliseconds since midnight, January 1, 1970
     * GMT.  Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access time.
     * This one gets updated whenever a request finishes.
     */
    public long getLastAccessedTime();

    /**
     * Return the last client access time without invalidation check
     * @see #getLastAccessedTime()
     */
    public long getLastAccessedTimeInternal();

    /**
     * Return the idle time (in milliseconds) from last client access time.
     */
    public long getIdleTime();

    /**
     * Return the idle time from last client access time without invalidation check
     * @see #getIdleTime()
     */
    public long getIdleTimeInternal();

    /**
     * Return the Manager within which this Session is valid.
     */
    public Manager getManager();


    /**
     * Set the Manager within which this Session is valid.
     *
     * @param manager The new Manager
     */
    public void setManager(Manager manager);

    /**
     * Return the maximum time interval, in seconds, between client requests
     * before the servlet container will invalidate the session.  A negative
     * time indicates that the session should never time out.
     * @return
     */
    int getMaxInactiveInterval();

    /**
     * The SessionEvent event type when a session is passivated.
     */
    public static final String SESSION_PASSIVATED_EVENT = "passivateSession";


    /**
     * Set the maximum time interval, in seconds, between client requests
     * before the servlet container will invalidate the session. A negative
     * time indicates that the session should never time out
     *
     * @param interval
     */
    void setMaxInactiveInterval(int interval);

    /**
     * Set the <code>isNew</code> flag for this session
     * @param isNew
     */
    void setNew(boolean isNew);


    /**
     * Return the authenticated Principal that is associated with this Session.
     * This provides an <code>Authenticator</code> with a means to cache a
     * previously authenticated Principal, and avoid potentially expensive
     * <code>Realm.authenticate()</code> calls on every request.  If there
     * is no current associated Principal, return <code>null</code>.
     */
    public Principal getPrincipal();


    /**
     * Set the authenticated Principal that is associated with this Session.
     * This provides an <code>Authenticator</code> with a means to cache a
     * previously authenticated Principal, and avoid potentially expensive
     * <code>Realm.authenticate()</code> calls on every request.
     *
     * @param principal The new Principal, or <code>null</code> if none
     */
    public void setPrincipal(Principal principal);


    /**
     * Return the <code>HttpSession</code> for whic this object
     * is the facade.
     * @return
     */
    HttpSession getSession();

    /**
     * Set the <code>isValid</code> flag for this session
     * @param isValid
     */
    void setValid(boolean isValid);

    /**
     * Return the <code>isValid</code> flag for this session
     * @return
     */
    boolean isValid();

    /**
     * Update the accessed time information for this session. This method
     * should be called by the context when a request comes in for a particular
     * session, event if the application does not reference it.
     */
    void access();

    /**
     * Add a session event listener to this component
     * @param listener
     */
    void addSessionListener(SessionListener listener);

    /**
     * End access to the session
     */
    void endAccess();


    /**
     * Perform the internal processing required to invalidate this session,
     * without triggering an exception if the session has already expired
      */
    void expire();

    /**
     * Return the object bound with the specified the name to the internal notes
     * for this session, or <code>null</code> if no such binding exists
     * @return
     */
    Iterator<String> getNoteNames();

    /**
     * Release all object references, and initialize instance variables, in
     * preparation for resue of this object
     */
    void recycle();

    /**
     * Remove any object references, and initialize instance variables, in
     * preparation for reuse of this object
     * @param name
     */
    void removeNote(String name);

    /**
     * Remove a session event listener from this component
     * @param listener
     */
    void removeSessionListener(SessionListener listener);

    /**
     * Bind an object to a specified name in the internal notes associated
     * with this session, replacing any existing binding for this name
     * @param name
     * @param value
     */
    void setNote(String name, Object value);

    /**
     * Inform the listeners about the change session ID
     * @param newId
     * @param oldId
     * @param notifySessionListeners Should any associated sessionListeners be
     *                               notified that session ID has been changed
     * @param notifyContainerListeners Should any associatedContainerListners
     *                                 be notified that session ID has been changed
     */
    void tellChangedSessionId(String newId, String oldId, boolean notifySessionListeners,
                boolean notifyContainerListeners);


    /**
     * Does the sessio implementation support the distributing of the given
     * attribute? If the Manager is marked as distributable, then this method
     * must be used to chaeck attributes before adding them to a session and
     * an {@link IllegalArgumentException} thrown if the proposed attribute is
     * not distribute
     *
     * Note that the {@link Manager} implementation may further restrict which
     * attribute are distributed but a {@link Manager} level restriction should
     * not triggeran {@link IllegalArgumentException} in
     * {@link javax.servlet.http.HttpSession#}
     *
     *
     * @param name
     * @param value
     * @return
     */
    boolean isAttributeDistributable(String name, Object value);

}
