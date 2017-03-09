package com.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * Represents a set of resiurces that are part of a web application. Examples
 * include a directory structure, a resources JAR and WAR file
 *
 * Created by xjk on 3/6/17.
 */
public interface WebResourceSet extends Lifecycle {

    /**
     * Obtain the object that represent resource at the given path. Note
     * the resource at that path may not exist
     * @param path
     * @return
     */
    WebResource getResource(String path);

    /**
     * Obtain the list of the names of all of the files and directories located
     * in the specified directory
     * @param path
     * @return The list of resources. If path does not refer to a directory
     *          then a zero length array will be returned
     */
    String[] list(String path);

    /**
     * Obtain the Set of the web applications pathnames of all of the files and
     * directories located in the specified directory. Paths representing
     * directories will end with a "/" character
     *
     * @param path
     * @return The set of resources. If path does not refer to a directory
     *          then an empty set will returned
     */
    Set<String> listWebAppPaths(String path);

    /**
     * Create a new directory at the given path
     *
     * @param path
     * @return <code>true</code> if the directory was created. otherwise
     *          <code>false</code>
     */
    boolean mkdir(String path);

    /**
     * Create a new resource at the requested path using the provided
     * InputStream
     *
     * @param path
     * @param is
     * @param overwrite
     * @return <code>true</code> if and only if the new Resource is written
     */
    boolean write(String path, InputStream is, boolean overwrite);


    void setRoot(WebResourceRoot root);

    /**
     *
     * @return
     */
    boolean getClassLoaderOnly();

    void setClassLoaderOnly(boolean classLoaderOnly);

    /**
     *
     *
     * @return
     */
    boolean getStaticOnly();

    void setStaticOnly(boolean staticOnly);

    URL getBaseUrl();

    /**
     * Configures whether or not this set of resources is read-only
     * @param readOnly
     */
    void setReadOnly(boolean readOnly);

    /**
     * Obtain this current value of the read-only setting for this set of
     * resource
     *
     * @return <code>true</code> if this set of resources is configured to be
     *      read-only. otherwise <code>false</code>
     */
    boolean isReadOnly();

    /**
     * Implementations may cache some information to improve performance. This
     * method triggers the clean-up of those resources
     */
    void gc();
}
