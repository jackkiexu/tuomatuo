package com.apache.catalina;

/**
 * Created by xjk on 3/6/17.
 */
public interface SessionIdGenerator {

    /**
     * Return the node identifier associated with this node which will be
     * included in the generated session ID
     * @return
     */
    String getJvmRoute();

    /**
     * Specify the node identifier associated with this node which will be
     * included in the generated session ID
     * @param jvmRoute
     */
    void setVmRoute(String jvmRoute);

    /**
     * Return the number of bytes for a session ID
     * @return
     */
    int getSessionIdLength();

    /**
     * Specify the number of bytes for a session ID
     * @param sessionIdLength
     */
    void setSessionIdLength(int sessionIdLength);

    /**
     * Generate and return a new session identifier
     * @return
     */
    String generateSessionId();

    /**
     * Generate and return a new session identifier
     * @param route
     * @return
     */
    String generateSessionId(String route);
}
