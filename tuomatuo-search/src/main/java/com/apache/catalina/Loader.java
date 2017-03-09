package com.apache.catalina;

import java.beans.PropertyChangeListener;

/**
 *
 * A Loader represents a Java ClassLoader implementation that can
 * be used by a Container to load class files (within a repository associated
 * with the Loader) that are designed to be reloaded upon request, as well as
 * a mechanism to detect whether changes have occurred in the underlying
 * repository
 *
 * In order for a <code>Loader</code> implementation to successfully operate
 * with a <code>Context</code> implementation that omplements reloading, it
 * must obey the following constraints:
 *
 * Must implement <code>Lifecycle</code> so that the Context can indicate
 * that a new class loader is required.
 * The <code>start()</code> method must unconditionally create a new
 * <code>ClassLoader</code> implementation
 * The <code>stop()</code> method must throw away its reference to the
 * <code>ClassLoader</code> previously untilized, sothat the class loader,
 * all classes loaded by it, and all objects of those classes, can be
 * garbage collected
 *
 * Must allow a call to <code>stop()</code> to be followed by a call to
 * <code>start()</code> to the same <code>Loader</code> instance
 *
 * Based on a policy chosen by the implementation, must call the
 * <code>Context.reload</code> method on the owning <code>Context</code>
 * when a change to one or more of the class files loaded by this class
 * loader is detected
 *
 * Created by xjk on 3/6/17.
 */
public interface Loader {


    /**
     * Execute a periodic task, such as reloading, etc. This method will be
     * invoked inside the classloading context of this container. Unexpected
     * throwables will be caught and logged
     */
    void backgroundProcess();

    /**
     * The Java loader to be used by this Container
     * @return
     */
    ClassLoader getClassLoader();

    /**
     * Set the Context with which this Loader has been associated.
     * @param context
     */
    void setContext(Context context);

    /**
     * the "follow standard delegation model" flag used to configure
     * our ClassLoader
     * @return
     */
    boolean getDelegate();

    /**
     * Set the following standard delegation model flag used to configure
     * our ClassLoader
     * @param delegate
     */
    void setDelegate(boolean delegate);

    /**
     * reloadable flag for this loader
     * @return
     */
    boolean getReloadable();

    /**
     * Set the reloadable flag for this Loader
     * @param reloadable
     */
    void setReloadable(boolean reloadable);

    /**
     * Add a property change listener ot this component
     * @param listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Has the internal repository associated with this Loader been modified
     * such that the loaded classes should be reloaded?
     * @return
     */
    boolean modified();

    /**
     * Remove a property change listener from this component
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
