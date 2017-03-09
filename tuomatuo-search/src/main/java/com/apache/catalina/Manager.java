package com.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * A <b>Manager</b> manages the pool of Sessions that are associated with a
 * particular Context. Different Manager implementations may supporte
 * value-added features such as the persistent  storage of session data
 * as well as migrating session for distributable web application
 *
 * In order for a <code>Manager</code> implementation to successfully operate
 * with a <code>Context</code> implements reloading. it
 * must obey the following constraints
 * Must implement <code>Lifecycle</code> so that the Context can indicate
 * that a restart us required
 * Must allow a call to <code>stop()</code>  to be followed by a call to
 * <code>start()</code> on the same <code>Manager</code> instance
 *
 * Created by xjk on 3/6/17.
 */
public interface Manager {


    // ------------------------------------------------------------- Properties


    /**
     * Return the Container with which this Manager is associated.
     *
     * @deprecated Use {@link #getContext()}. This method will be removed in
     *             Tomcat 9 onwards.
     */
    @Deprecated
    public Container getContainer();


    /**
     * Set the Container with which this Manager is associated.
     *
     * @param container The newly associated Container
     *
     * @deprecated Use {@link #setContext(Context)}. This method will be removed in
     *             Tomcat 9 onwards.
     */
    @Deprecated
    public void setContainer(Container container);


    /**
     * Return the Context with which this Manager is associated.
     * @return
     */
    Context getContext();


    /**
     * Set the Context with which this Manager is associated. The Context must
     * be set to a non-null before the Manager is first used. Multiple
     * calls to this method before first use are permitted. Once the Manager has
     * been used. this method may be not used to change the Context (including
     * setting a {@code null} ) that the Manager is associated with
     * @param context
     */
    void setContext(Context  context);


    /**
     * Return the distributable flag for the sessions supported by
     * this Manager.
     *
     * @deprecated Ignored. {@link Context#getDistributable()} always takes
     *             precedence. Will be removed in Tomcat 8.5.x.
     */
    @Deprecated
    public boolean getDistributable();


    /**
     * Set the distributable flag for the sessions supported by this
     * Manager.  If this flag is set, all user data objects added to
     * sessions associated with this manager must implement Serializable.
     *
     * @param distributable The new distributable flag
     *
     * @deprecated Ignored. {@link Context#getDistributable()} always takes
     *             precedence. Will be removed in Tomcat 8.5.x.
     */
    @Deprecated
    public void setDistributable(boolean distributable);


    /**
     * Return the default maximum inactive interval (in seconds)
     * for Sessions created by this Manager.
     *
     * @deprecated Ignored. {@link Context#getSessionTimeout()} always takes
     *             precedence. Will be removed in Tomcat 8.5.x.
     */
    @Deprecated
    public int getMaxInactiveInterval();


    /**
     * Set the default maximum inactive interval (in seconds)
     * for Sessions created by this Manager.
     *
     * @param interval The new default value
     *
     * @deprecated Ignored. {@link Context#getSessionTimeout()} always takes
     *             precedence. Will be removed in Tomcat 8.5.x.
     */
    @Deprecated
    public void setMaxInactiveInterval(int interval);


    /**
     * return the session id generator
     * @return
     */
    SessionIdGenerator getSessionIdGenerator();

    /**
     * Set the session id generator
     * @param sessionIdGenerator
     */
    void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator);

    /**
     * Sets the session id length(in bytes) for Sessions created by this
     * Manager
     * @param idLength
     */
    void setSessionIdLength(int idLength);


    /**
     * Return the total number of sessions created by this manager
     * @return
     */
    long getSessionCounter();

    /**
     * Sets the total number of sessions created by this manager
     * @param sessionCounter
     */
    void setSessionCounter(long sessionCounter);

    /**
     * Gets the maximum number of sessions that have been active at the same
     * time.
     * @return
     */
    int getMaxActive();

    /**
     * Sets the maximum number of sessions that have been active at the
     * same time
     * @param maxActive
     */
    void setMaxActive(int maxActive);

    /**
     * Get the number of currently active sessions
     * @return
     */
    int getActiveSessions();

    /**
     * Gets the number of sessions that have expired
     * @return
     */
    long getExpiredSessions();

    /**
     * Sets the number of sessions that have expired
     * @param expiredSessions
     */
    void setExpiredSessions(long expiredSessions);


    /**
     * Get the number of sessions that were not created because the maximum
     * number of active sessions was reached
     * @return
     */
    int getRejectedSession();

    /**
     * Gets the longest time (in seconds) that an expired session had been
     * alive
     * @return
     */
    int getSessionMaxAliveTime();

    /**
     * Sets the longest time (in seconds) that an expired session had been
     * alive
     * @param sessionMaxAliveTime
     */
    void setSessionMaxAliveTime(int sessionMaxAliveTime);


    /**
     * Gets the average time (in seconds) that expired sessions had been
     * alive. This may be based on sample data
     *
     * @return Average time (in seconds ) that expired sessions had ben
     * alive
     */
    int getSessionAverageAliveTime();

    /**
     * Gets the current rate of session creation(in session per minute). This
     * may be based on sample data
     *
     * @return The current rate (in sessions per minute) of session creation
     */
    int getSessionCreateRate();

    /**
     * Gets the current rate of session expiration (in session per minute). This
     * may be based on sample data
     * @return The current rate (in session per minute) of session expiration
     */
    int getSessionExpireRate();

    /**
     * add this session to the set of active Sessions for this Manager
     * @param session
     */
    void add(Session session);

    /**
     * Add a property change listener to this component
     * @param listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Change the session ID of the current session to a new randomly generated
     * @param session
     */
    void changeSessionId(Session session);

    /**
     * Change the session ID of the current session to a specified session ID.
     * @param session
     * @param newId
     */
    void changeSessionId(Session session, String newId);

    /**
     * Get a session from the recycled ones or create a new empty one.
     * The persistentManager manager does not need to create session data
     * because it reads it from the Store
     * @return
     */
    Session createEmptySessin();

    /**
     * Construct and return a new session object, based on the default
     * setting specified by this Manager's properties. The session
     * is specified will be used as the session is
     * If a new session cannot be created for any reason, return
     * null
     * @param sessionId
     * @return
     */
    Session createSession(String sessionId);


    /**
     * Return the active Session, associated with this Manager, with the
     * specified session id (if any); otherwise return <code>null</code>
     * @param id
     * @return
     * @throws IOException
     */
    Session findSession(String id) throws IOException;

    /**
     * Return the set of active Sessions associated with this Manager
     * If this Manager has no active Sessions, a zero-length array is returned
     * @return
     */
    Session[] findSessions();


    /**
     * Load any currently active sessions that were previously unload
     * to the appropriate persistence mechanism, if any. If persistence is not
     * supported, this method returns without doing anything
     * @throws ClassNotFoundException
     * @throws IOException
     */
    void  load() throws ClassNotFoundException, IOException;

    /**
     * Remove this session from the active Sessions for this Manager
     * @param session
     */
    void remove(Session session);

    /**
     * Remove this session from the active Sessions for this Manager
     * @param session
     * @param update Should the expiration statistics be updated
     */
    void remove(Session session, boolean update);


    /**
     * Remove a property change listener from this component
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Save any currently active sessions in the approriate persistence
     * mechanism, if any. If persistence is not supported, this method
     * returns without doing anything
     * @throws IOException
     */
    void unload() throws IOException;


    /**
     * This method will be invoked by the context/container on a periodic
     * basis and allows the manager to implement
     * a method that executes periodic tasks, such as expiring session etc.
     */
    void backgroundProcess();

    /**
     * Would the Manager distribute the given session attribute? Manager
     * implementations amy provide additional configuration options to control
     * which attributes are distributable
     *
     * @param name
     * @param value
     * @return
     */
    boolean willAttributeDistribute(String name, Object value);

}
