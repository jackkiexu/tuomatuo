package com.apache.catalina;

import java.security.Principal;
import java.util.Iterator;

/**
 * Created by xjk on 3/6/17.
 */
public interface Group extends Principal{

    // ------------------------------------------------------------- Properties


    /**
     * Return the description of this group.
     */
    public String getDescription();


    /**
     * Set the description of this group.
     *
     * @param description The new description
     */
    public void setDescription(String description);


    /**
     * Return the group name of this group, which must be unique
     * within the scope of a {@link UserDatabase}.
     */
    public String getGroupname();


    /**
     * Set the group name of this group, which must be unique
     * within the scope of a {@link UserDatabase}.
     *
     * @param groupname The new group name
     */
    public void setGroupname(String groupname);


    /**
     * Return the set of {@link Role}s assigned specifically to this group.
     */
    public Iterator<Role> getRoles();


    /**
     * Return the {@link UserDatabase} within which this Group is defined.
     */
    public UserDatabase getUserDatabase();


    /**
     * Return the set of {@link User}s that are members of this group.
     */
    public Iterator<User> getUsers();


    // --------------------------------------------------------- Public Methods


    /**
     * Add a new {@link Role} to those assigned specifically to this group.
     *
     * @param role The new role
     */
    public void addRole(Role role);


    /**
     * Is this group specifically assigned the specified {@link Role}?
     *
     * @param role The role to check
     */
    public boolean isInRole(Role role);


    /**
     * Remove a {@link Role} from those assigned to this group.
     *
     * @param role The old role
     */
    public void removeRole(Role role);


    /**
     * Remove all {@link Role}s from those assigned to this group.
     */
    public void removeRoles();
}
