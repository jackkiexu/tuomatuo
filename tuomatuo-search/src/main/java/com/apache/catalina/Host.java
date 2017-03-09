package com.apache.catalina;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 *
 * A Host is Container that represents a virtual host in the
 * Catalina servlet engine. It is useful in the following types of scenarios:
 * You wish to use Interceptors that see every single request processed
 * by this particular virtual host
 * You wish to run Catalina in with s standalone HTTP connector, but still
 * want support for multiple virtual hosts
 *
 * In general, you would not use a Host when deploying Catalina connected
 * to a web server(such as Apache), because the Connector will have
 * utilized the web server's facilities to determine which Context(or
 * perhaps even which Wrapper) should be utilized to process this request
 * The parent Container attached to a Host is generally an Engine, but may
 * be some other implementation, or may be omitted if it not necessary.
 *
 * The child containers attched to a Host are generally implementations
 * of Context (representing an individual servlet context)
 *
 * Created by xjk on 3/6/17.
 */
public interface Host extends Container {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The ContainerEvent event type sent when a new alias is added
     * by <code>addAlias()</code>.
     */
    public static final String ADD_ALIAS_EVENT = "addAlias";


    /**
     * The ContainerEvent event type sent when an old alias is removed
     * by <code>removeAlias()</code>.
     */
    public static final String REMOVE_ALIAS_EVENT = "removeAlias";


    public String getXmlBase();
    /**
     *
     * @param xmlBase
     */
    void setXmlBase(String xmlBase);

    /**
     * Return a default configuration path of this Host. The file will be
     * canonical if possible
     * @return
     */
    File getConfigBaseFile();

    /**
     * Return the application root for this Host. This can be an absolute
     * pathname, a relative pathname, or a URL
     * @return
     */
    String getAppFile();

    /**
     * Returns an absolute {@link File } for the appBase of this Host. The file
     * will be canonical if possible. There is no guarantee that the
     * appBase exists
     * @return
     */
    File getAppBaseFile();

    /**
     * Set the application root for this Host. This can be an absolute
     * pathname, a relative pathname, or a URL
     * @param appBase
     */
    void setAppBase(String appBase);

    /**
     * Returns the value of the auto deploy flag. If true, it indicates that
     * this host's child webapps should be discovered and automatically
     * deployed dynamically
     * @return
     */
    boolean getAutoDeploy();

    /**
     * Set the auto deploy flag value for this host
     * @param autoDeploy
     */
    void setAutoDeploy(boolean autoDeploy);

    /**
     * Return the Java class name of the context configuration class
     * for new web applications
     * @return
     */
    String getConfigClass();

    /**
     * Set the Java class name of the context configuration class
     * for new web applications
     * @param configClass
     */
    void setConfigClass(String configClass);

    /**
     * Return the value of the deploy on startup flag. If true, it indicates
     * that this host's child webapps should be discovered and automatically
     * deployed
     * @return
     */
    boolean getDeployOnStartup();

    /**
     * Set the deploy on startup flag value for this host
     * @param deployOnStartup
     */
    void setDeployOnStartup(boolean deployOnStartup);

    /**
     * Return the regular expression that defines the fles and directories in
     * the host's appBase that will be ignored by the automatic deployment
     * process
     * @return
     */
    String getDeployIgnore();

    /**
     *Return the compiled regular expression that defines the files and
     * direcotries in the host's appBase that will be ignored by the automatic
     * deployment process
     * @return
     */
    Pattern getDeployIgnorePattern();

    /*
    Set the regular expression that defines the files and directories in
    the host's appBase that will be ignored by the automatic deployment
    process
     */
    void setDeployIgnore(String deployIgnore);

    /**
     * Return the executor that is used for starting and stoppng contexts. This
     * is primarily for use by components deploying contexts that want to do
     * this in a multi-threaded manner
     * @return
     */
    ExecutorService getStartStopExecutor();

    /**
     * Returns true if the Host will attempt to create directories for appBase and xmlBase
     * unless they already exist
     *
     * @param createDirs
     * @return true if the Host will attempt to create directories
     */
    boolean setCreateDirs(boolean createDirs);

    /**
     * Returns true of the Hosts is configured to automatically undeploy old
     * versions of applications deployed using parallel deployment. This only
     * takes effect is {@link #getAutoDeploy()} also returns true
     * @return
     */
    boolean getUndeployOldVersions();

    /**
     * Set to true if the Host should automatically undeploy old versions of
     * applications deployed using parallel deployment. This only takes effect
     * if {@link #getAutoDeploy()} returned true
     * @param undeployOldVersions
     */
    void setUndeployOldVersions(boolean undeployOldVersions);

    /**
     * Add an alias name that should be mapped to this same Host
     * @param alias
     */
    void addAlias(String alias);


    /**
     * Return the set of alias names for this Host. If none are defined,
     * a zero length array is returned.
     * @return
     */
    String[] findAliases();

    /**
     * Remove the specified alias name from the aliases for this Host
     * @param alias
     */
    void removeAlias(String alias);

}
