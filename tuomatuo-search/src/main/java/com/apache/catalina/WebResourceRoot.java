package com.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by xjk on 3/6/17.
 */
public interface WebResourceRoot extends Lifecycle {


    /**
     * Obtain the object that represents the resource at the given path. Note
     * that the resource at that path may not exist. If the path does not
     * exist, the WebResource returned will be associated with the main
     * WebResourceSet.
     *
     * @param path  The path for the resource of interest relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  The object that represents the resource at the given path
     */
    WebResource getResource(String path);

    /**
     * Obtain the objects that represent the resource at the given path. Note
     * that the resource at that path may not exist. If the path does not
     * exist, the WebResource returned will be associated with the main
     * WebResourceSet. This will include all matches even if the resource would
     * not normally be accessible (e.g. because it was overridden by another
     * resource)
     *
     * @param path  The path for the resource of interest relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  The objects that represents the resource at the given path
     */
    WebResource[] getResources(String path);

    /**
     * Obtain the object that represents the class loader resource at the given
     * path. WEB-INF/classes is always searched prior to searching JAR files in
     * WEB-INF/lib. The search order for JAR files will be consistent across
     * subsequent calls to this method until the web application is reloaded. No
     * guarantee is made as to what the search order for JAR files may be.
     *
     * @param path  The path of the class loader resource of interest relative
     *              to the the root of class loader resources for this web
     *              application.
     *
     * @return  The object that represents the class loader resource at the
     *          given path
     */
    WebResource getClassLoaderResource(String path);

    /**
     * Obtain the objects that represent the class loader resource at the given
     * path. Note that the resource at that path may not exist. If the path does
     * not exist, the WebResource returned will be associated with the main
     * WebResourceSet. This will include all matches even if the resource would
     * not normally be accessible (e.g. because it was overridden by another
     * resource)
     *
     * @param path  The path for the class loader resource of interest relative
     *              to the root of the class loader resources for the web
     *              application. It must start with '/'.
     *
     * @return  The objects that represents the class loader resources at the
     *          given path
     */
    WebResource[] getClassLoaderResources(String path);

    /**
     * Obtain the list of the names of all of the files and directories located
     * in the specified directory.
     *
     * @param path  The path for the resource of interest relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  The list of resources. If path does not refer to a directory
     *          then a zero length array will be returned.
     */
    String[] list(String path);

    /**
     * Obtain the Set of the web applications pathnames of all of the files and
     * directories located in the specified directory. Paths representing
     * directories will end with a '/' character.
     *
     * @param path  The path for the resource of interest relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  The Set of resources. If path does not refer to a directory
     *          then null will be returned.
     */
    Set<String> listWebAppPaths(String path);

    /**
     * Obtain the list of all of the WebResources in the specified directory.
     *
     * @param path  The path for the resource of interest relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  The list of resources. If path does not refer to a directory
     *          then a zero length array will be returned.
     */
    WebResource[] listResources(String path);

    /**
     * Create a new directory at the given path.
     *
     * @param path  The path for the new resource to create relative to the root
     *              of the web application. It must start with '/'.
     *
     * @return  <code>true</code> if the directory was created, otherwise
     *          <code>false</code>
     */
    boolean mkdir(String path);


    boolean write(String path, InputStream is, boolean overwrite);


    void createWebResourceSet(ResourceSetType type, String webAppMount, URL url, String internalPath);


    void createWebResourceSet(ResourceSetType type, String webAppMount, String base, String archivePath,
                            String internalPath);

    /**
     * Adds the provided WebResourceSet to this web appication as a 'Pre'
     * resource
     * @param webResourceSet
     */
    void addPreResources(WebResourceSet webResourceSet);


    /**
     * Get the list of WebResourceSet configured to this web application
     * as a 'Pre' resource
     * @return
     */
    WebResourceSet[] getPreResources();

    /**
     * Adds the provided WebResourceSet configured to this web application
     * as a 'Jar' rresource
     * @param webResourceSet
     */
    void addJarResources(WebResourceSet webResourceSet);

    /**
     * Get the list of WebResourceSet configured to this web application
     * as a 'Jar' resource
     * @return
     */
    WebResourceSet[] getJarResources();

    /**
     * Adds the provided WebResourceSet to this web application as a 'POST'
     * resource
     * @param webResourceSet
     */
    void addPostResources(WebResourceSet webResourceSet);


    /**
     * Get the list of WebResourceSet configured to this web application
     * as a "POST" resource
     * @return
     */
    WebResourceSet[] getPostResources();

    /**
     * Obtain the web application this WebResourceRoot is associated with
     * @return
     */
    Context getContext();

    /**
     * Set the web application this WebResourcRoot is associated with
     * @param context
     */
    void setContext(Context context);

    /**
     * Configure if this resources allow the use of symbolic links
     * @param allowLinking
     */
    void setAllowLinking(boolean allowLinking);

    /**
     * Determine if this resources allow the use of symbolic links
     * @return
     */
    boolean getAllowLinking();


    /**
     * Set whether or not caching is permitted for this web application
     * @param cachingAllowed
     */
    void setCachingAllowed(boolean cachingAllowed);


    /**
     * Get whether or not caching is permitted for this web application
     * @return
     */
    boolean isCachingAllowed();


    /**
     * Set the Time-To-Live (TTL) for cache entries
     * @param ttl
     */
    void setCacheTtl(long ttl);


    /**
     * Get the Time-To-Live(TTL) for cache entries
     * @return
     */
    long getCacheTtl();

    /**
     * Set the maximum permitted size for the cache
     * @param cacheMaxSize
     */
    void setCacheMaxSize(long cacheMaxSize);

    /**
     * Get the maximum permitted size for the cache
     * @return
     */
    long getCacheMaxSize();

    /**
     * Set the maximum permitted size for a single object in the cache. Note
     * that the maximum size in bytes may not exceed {@link Integer#MAX_VALUE}
     * @param cacheObjectMaxSize
     */
    void setCacheObjectMaxSize(int cacheObjectMaxSize);

    /**
     * Get the maximum permitted size for a single object in the cache. Note
     * that the maximum size in bytes may not exceed {@link Integer#MAX_VALUE}
     * @return Maximum size for a single cached object in kilobytes
     */
    int getCacheObjectMaxSize();

    /**
     * Controls whether the track locked files feature is enabled. If enabled
     * all calls to methods that return objects that lock a file and need to be
     * closed to release that lock
     * will perform a number of additional tasks
     * The stack trace at the point where the menthod was called will be
     * recorded and associated with the returned object
     * The returned object will be wrapped sot that the point where close()
     * si called to release the resources can be detected
     * Tracking of the object will cease the recources have been
     * released
     * All remaining locked resources on web application shutdown will be
     * logged and then closed
     * @param trackLockedFiles
     */
    void setTrackLockedFiles(boolean trackLockedFiles);

    /**
     * Has the track locked files feature been enabled?
     * @return
     */
    boolean getTrackLockedFiles();

    /**
     * This method will be invoked by the context on a periodic basis and allows
     * the omplementation a method that executes periodic tasks. such as purging
     * expired cache entries
     */
    void backgroundProcess();

    void registerTrackedResource(TrackedWebResource trackedWebResource);

    void deregisterTrackedResource(TrackedWebResource trackedWebResource);

    /**
     * Obtain the set of {@link WebResourceSet$getBaseUrl} for all
     * {@link WebResourceSet}s used by this root
     * @return
     */
    List<URL> getBaseUrls();

    /**
     * Implementations may cache some information to improve performance. This
     * method triggers the clean-up of those resources
     */
    void gc();

    static enum ResourceSetType{
        PRE,
        RESOURCE_JAR,
        POST,
        CLASSES_JAR
    }

}
