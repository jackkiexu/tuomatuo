package com.apache.catalina;

import com.apache.catalina.deploy.NamingResourcesImpl;
import com.apache.catalina.startup.Catalina;

import javax.naming.*;
import java.io.File;

/**
 * A <code>Server</code> element represents the entire Catalina
 * servlet container. Its attributes represent the characteristics of
 * the servlet container as a whole. A <code>Server</code> may contain
 * one or more <code>Services</code>, and the top level set of naming
 * resources
 *
 * Normally, an implementation of this interface will also implement
 * <code>Lifecycle</code>, such that when the <code>start()</code> and
 * <code>stop()</code> methods are called. al of the defined
 * <code>Services</code> are also started or stopped
 *
 * In between, the mplementation must open a server socket on the port number
 * specified by the <code>port</code> property. When a connection is accepted,
 * the first line is read and compared with the specified shutdown command.
 * If the command matches, shutdown of the server is initiated
 *
 * <strong>NOTE</strong> The concrete implementation of this class should
 * register the ()singleton instance with the <code>ServerFactory</code>
 * class in its constructor
 *
 *
 * Created by xjk on 3/6/17.
 */
public interface Server extends Lifecycle{


    /**
     *
     * @return the global naming resources
     */
    NamingResourcesImpl getGlobalNamingResources();

    /**
     * Set the global naming resources
     * @param globalNamingResources
     */
    void setGlobalNamingResources(NamingResourcesImpl globalNamingResources);

    /**
     * the global naming resources context
     * @return
     */
    javax.naming.Context getGlobalNamingContext();

    /**
     * the global naming resources context
     * @return
     */
    int getPort();

    /**
     * the port number we listen to for shutdown commands
     * @param port
     */
    void setPort(int port);

    /**
     * Set the address on which we listen to for shutdown commands
     * @return
     */
    String getAddress();

    /**
     * Set the address on which we listen to for shutdown commands
     * @param address
     */
    void setAddress(String address);

    /**
     * the shutdown command string we are waiting for
     * @return
     */
    String getShutdown();

    /**
     * Set the shutdown command we are waiting for
     * @param shutdown
     */
    void setShutdown(String shutdown);


    /**
     * @return the parent class loader for this component. If not set, return
     * {@link #getCatalina()} {@link Catalina#getParentClassLoader} if
     * catalina has not been set, return the system class loader
     */
    ClassLoader getParentClassLoader();

    /**
     * the outer Catalina startup/shutdown component if present
     * @return
     */
    Catalina getCatalina();


    void setCatalina(Catalina catalina);

    /**
     * @return the configured base (instance) directory. Note that home and base
     * may be the same (and are by default). If this is not set the value
     * returned by {@link #getCatalinaHome()} will be used.
     */
    File getCatalinaBase();

    /**
     * Set the configured base (instance) directory. Note that home and base
     * may be the same (and are by default)
     * @param catalinaBase
     */
    void setCatalinaBase(File catalinaBase);
    /**
     * @return the configured home (binary) directory. Note that home and base
     * may be the same (and are by default).
     */
    File getCatalinaHome();

    /**
     * Set the configured home(binary) directory. Note that home and base
     * may be the same (and are by default)
     * @param catalineHome
     */
    void setCatalinaHome(File catalineHome);

    /**
     * Add a new Service t the set of defined Services
     * @param service
     */
    void addService(Service service);

    /**
     * Wait until a proper shutdown command is received, then return
     */
    void await();

    /**
     * Find the specified Service
     * @param name
     * @return
     */
    Service findService(String name);


    /**
     * @return the set of Services defined within this Server
     */
    Service[] findServices();

    /**
     * Remove the specified Service from the set associated from this
     * Server
     * @param service
     */
    void removeService(Service service);

    /**
     * @return the token necessary for operations on the associated JNDI naming
     *          context
     */
    Object getNamingToken();

}
