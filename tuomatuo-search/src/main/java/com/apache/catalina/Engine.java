package com.apache.catalina;

/**
 * An Engine is a Container that represents the entire Catalina servlet
 * engine. It is useful in the following type of scenarios:
 * You wish to use Interceptors that see every single request processed
 * by the entire engine
 * You wish to run Catalina in with astandalone HTTP connector, but still
 * want support for multiple virtual hosts
 *
 * In general, you would not use an Engine when deploying Catalina connected
 * to a web server(Such as Apache), because the Connector will have
 * utilized the web server's facilities to determine which Context (or
 * perhaps even which Wrapper) should be utilized to process this request
 *
 * The child containers attached to an Engine are generally implementations
 * of Host(representing a virtual host) or Context (representing individual
 * an individual servlet context), depending upon the Engine implementation
 *
 * If used, an Engine is always the top level Container in a Catalina
 * hierachy. Therefore, the implementation's <code>setParent</code> method
 * should throw <code>IllegalArgumentException</code>
 *
 *
 * Created by xjk on 3/6/17.
 */
public interface Engine extends Container {


    /**
     * Return the default hostname for this Engine
     * @return
     */
    String getDefaultHost();

    /**
     * Set the default hostname for this Engine
     * @param defaultHost
     */
    void setDefaultHost(String defaultHost);

    /**
     * Retrieve the JvmRouteId for this engine
     * @return
     */
    String getJvmRoute();

    /**
     * Set the JvmRouteId for this engine
     * @param jvmRouteId the JVM Route ID. Each Engine within a cluster
     *                     must have a unique JVM Route ID.
     */
    void setJvmRoute(String jvmRouteId);

    /**
     * Return the <code>Service</code> with which we are associated(if any)
     * @return
     */
    Service getService();

    /**
     * Set the <code>Service</code> with which we are associated(if any)
     * @param service The service that owns this Engine
     */
    void setService(Service service);
}
