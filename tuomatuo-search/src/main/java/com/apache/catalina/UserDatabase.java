package com.apache.catalina;

import java.util.Iterator;

/**
 * Abstract representation of a database of {@link User}s and
 * {@link Group}s that can be maintained by an application,
 * along with definitions of corresponding {@link Role}s, and
 * referenced by a {@link Realm} for authentication and access control
 *
 * Created by xjk on 3/6/17.
 */
public interface UserDatabase {


    // ------------------------------------------------------------- Properties


    /**
     * Return the set of {@link Group}s defined in this user database.
     */
    public Iterator<Group> getGroups();


    /**
     * Return the unique global identifier of this user database.
     */
    public String getId();


    /**
     * Return the set of {@link Role}s defined in this user database.
     */
    public Iterator<Role> getRoles();


    /**
     * Return the set of {@link User}s defined in this user database.
     */
    public Iterator<User> getUsers();

    /**
     * Finalize access to this user database
     * @throws Exception
     */
    void close() throws Exception;


    /**
     * Create and return a new {@link Group} defined in this user database.
     * @param groupname
     * @param description
     * @return
     */
    Group createGroup(String groupname, String description);

    /**
     * Create and return a new {@link Role} defined in this user database.
     * @param rolename
     * @param description
     * @return
     */
    Role createRole(String rolename, String description);


    /**
     * Create and return a new {@link User} defined in this user database.
     * @param username
     * @param password
     * @param fullName
     * @return
     */
    User createUser(String username, String password, String fullName);


    /**
     * Return the {@link Group} with the specified group name, if any;
     * otherwise return <code>null</code>.
     *
     * @param groupname Name of the group to return
     * @param groupname
     * @return
     */
    Group findGroup(String groupname);

    /**
     * Return the {@link Role} with the specified role name, if any
     * other wise return <code>null</code>
     * @param rolename
     * @return
     */
    Role findRole(String rolename);

    /**
     * Return the {@link Role} with specified user name, if any
     * otherwise return <code>null</code>
     * @param username
     * @return
     */
    User findUser(String username);


    /**
     * Initialize access to this user database.
     * @throws Exception
     */
    void open() throws Exception;

    /**
     * Remove the specified {@link Role} from this user database.
     * @param role
     */
    void removeRole(Role role);

    /**
     * Remove the specified {@link User} from this user database.
     * @param role
     */
    void removeUser(Role role);

    /**
     * Remove the specified {@link User} from this user database
     * @param user
     */
    void removeUser(User user);

    /**
     * Save any updated information to the persistent storage location for
     * this use database.
     * @throws Exception
     */
    void save() throws Exception;
}
