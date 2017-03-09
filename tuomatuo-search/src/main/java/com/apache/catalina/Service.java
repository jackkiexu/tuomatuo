package com.apache.catalina;

import com.apache.catalina.connector.Connector;
import com.apache.catalina.mapper.Mapper;
import com.facebook.nifty.client.UnframedClientConnector;

import java.util.concurrent.*;

/**
 *
 * A <strong>Service</strong> is a group of one or more
 * <strong>Connectors</strong> that share a single <strong>Container</strong>
 * to process their incoming requests. This arrangement allows. For example
 * a non-ssl and SSL connector to share the same population of we apps
 *
 * A given JVM can conatin any number of Service instances; however, they are
 * completely independent f each other and share only the basic JVM facilities
 * and classes on the system class path
 *
 * Created by xjk on 3/6/17.
 */
public interface Service extends Lifecycle {


    /**
     * @return the <code>Container</code> that handles request for all
     * <code>Connectors</code> associated with this Service
     */
    Container getContainer();

    /**
     * Set the <code>Container</code> that handles requests for all
     * <code>Connectors</code> associated with this Service.
     * @param container
     */
    void setContainer(Container container);

    /**
     * Set the <code>Engine</code> that handles requests for all
     * <code>Connectors</code> associated with this Service
     * @param engine
     */
    void setContainer(Engine engine);

    /**
     * Get the name of this Service
     * @return
     */
    String getName();
    /**
     * Set the name of this Service
     * @param name
     */
    void setName(String name);

    /**
     * @return the <code>Server</code> with which we are associated (if any)
     */
    Server getServer();

    /**
     * Set the <code>Server</code> with which we are associated(if any)
     * @param server
     */
    void setServer(Server server);

    /**
     * @return return parent class loader for this component. If not set, return
     * {@link #getServer()} {@link Server#getParentClassLoader}. If no server
     * has been set, return system class loader
     */
    ClassLoader getParentClassLoader();

    /**
     * Set the parent class loader for this service
     * @param parent
     */
    void setParentClassLoader(ClassLoader parent);

    /**
     * @return the domain under which this container will be / has been
     * registered
     */
    String getDomain();

    /**
     * Add a new Connector to the set of defined Connectors, the associate it
     * with this Service's Container
     * @param connector
     */
    void addConnector(Connector connector);

    /**
     * Find and return the set of Connectors associated with this Service
     * @return
     */
    Connector[] findConnectors();

    /**
     * Remove the specified connector from the set associated from this
     * Service. The removed Connector will also disassociated from our
     * Container
     * @param connector
     */
    void removeConnector(Connector connector);

    /**
     * Adds a named executor to the
     * @param ex
     */
    void addExecutor(Executor ex);

    /**
     * Retrieves all executors
     * @return
     */
    Executor[] findExecutors();

    /**
     * Retrieves all executors
     * @param ex
     * @return
     */
    Executor getExecutor(Executor ex);

    /**
     * removes an executor from the service
     * @param ex
     */
    void removeExecutor(java.util.concurrent.Executor ex);

    /**
     * @return the mapper associated with this Service
     */
    Mapper getMapper();

}
