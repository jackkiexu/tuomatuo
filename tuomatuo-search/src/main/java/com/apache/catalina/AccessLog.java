package com.apache.catalina;

import com.apache.catalina.connector.Request;
import com.apache.catalina.connector.Response;

/**
 * Intended for use by a {@link Valve} to indicate that the {@link Valve}
 * provides access loging. It is used by the Tomcat internals to identify a
 * Value that logs access request so requests that are rejected
 * earlier in the processing chain can still be added to the access log
 * Implementations of this interface should be robust against the provided
 * {@link Request} and {@link Response} objects being null, having null
 * attributes or any other 'oddness' that may result from attempting to log
 * a request that was almost certainly rejected because it was mal-formed
 * Created by xjk on 3/6/17.
 */
public interface AccessLog {

    /**
     * Name of request attribute used to override the remote address recorded by
     * the AccessLog.
     */
    public static final String REMOTE_ADDR_ATTRIBUTE =
            "org.apache.catalina.AccessLog.RemoteAddr";

    /**
     * Name of request attribute used to override remote host name recorded by
     * the AccessLog.
     */
    public static final String REMOTE_HOST_ATTRIBUTE =
            "org.apache.catalina.AccessLog.RemoteHost";

    /**
     * Name of request attribute used to override the protocol recorded by the
     * AccessLog.
     */
    public static final String PROTOCOL_ATTRIBUTE =
            "org.apache.catalina.AccessLog.Protocol";

    /**
     * Name of request attribute used to override the server port recorded by
     * the AccessLog.
     */
    public static final String SERVER_PORT_ATTRIBUTE =
            "org.apache.catalina.AccessLog.ServerPort";




    /**
     * Add the request/response to the access log using the specified processing
     * time.
     *
     * @param request   Request (associated with the response) to log
     * @param response  Response (associated with the request) to log
     * @param time      Time taken to process the request/response in
     *                  milliseconds (use 0 if not known)
     */
    public void log(Request request, Response response, long time);

    /**
     * Should this valve set request attributes for IP address, Hostname,
     * protocol and port used for the request? This are typically used in
     * conjunction with the {@link org.apache.catalina.valves.AccessLogValve}
     * which will otherwise log the original values.
     * Default is <code>true</code>.
     *
     * The attributes set are:
     * <ul>
     * <li>org.apache.catalina.RemoteAddr</li>
     * <li>org.apache.catalina.RemoteHost</li>
     * <li>org.apache.catalina.Protocol</li>
     * <li>org.apache.catalina.ServerPost</li>
     * </ul>
     *
     * @param requestAttributesEnabled  <code>true</code> causes the attributes
     *                                  to be set, <code>false</code> disables
     *                                  the setting of the attributes.
     */
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled);

    /**
     * @see #setRequestAttributesEnabled(boolean)
     * @return <code>true</code> if the attributes will be logged, otherwise
     *         <code>false</code>
     */
    public boolean getRequestAttributesEnabled();
}
