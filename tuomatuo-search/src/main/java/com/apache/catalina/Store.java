package com.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * A Store is the abstraction of a Catalina component that provides
 * persistent storage and loading of Sessions and their associated user data.
 * Implementation are free to save and loading the Sessions to any media they
 * wish, but it is assumed that saved Sessions are persistent across
 * server or context restarts
 *
 * Created by xjk on 3/6/17.
 */
public interface Store {


    /**
     * Return the Manager instance associated with this Store
     * @return
     */
    Manager getManager();


    /**
     * Set the Manager associated with this Store
     * @param manager
     */
    void setManager(Manager manager);

    /**
     * Return the number of Sessions present in this Store
     * @return
     * @throws IOException
     */
    int getSize() throws IOException;

    /**
     * Add a property change listener to this component.
     * @param listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Return an array containing the session identifiers of all Sessions
     * currently saved in this Store. If there are no such Sessions, a
     * zero-length array is returned
     * @return
     * @throws IOException
     */
    String[] keys() throws IOException;

    /**
     * Load and return the Session associated with the specified session
     * identifier from this store, without removing it. If there is no
     * such stored Session, return <code>null</code>
     *
     * @param id Session identifier of the session to load
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    Session load(String id) throws ClassNotFoundException, IOException;

    /**
     * Remove the Session with the specified session identifier from
     * this Store, if present. If no such Session is present, this method
     * takes no action
     * @param id
     * @throws IOException
     */
    void remove(String id) throws IOException;

    /**
     * Remove all Session from this Store
     * @throws IOException
     */
    void clear() throws IOException;


    /**
     * Remove a property change listener from this component
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Save the spacified Session into this Store. Any previously saved
     * information for the associated session identifier is replaced
     * @param session
     * @throws IOException
     */
    void save(Session session) throws IOException;
}
