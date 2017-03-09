package com.apache.catalina;

import java.security.Principal;

/**
 * Abstract representation of a security role. suitable for use in
 * environments like JAAS that want to deal with <code>Principals</code>
 * Created by xjk on 3/6/17.
 */
public interface Role extends Principal {



    /**
     * @return the description of this role.
     */
    public String getDescription();


    /**
     * Set the description of this role.
     *
     * @param description The new description
     */
    public void setDescription(String description);

    /**
     * the role name of this role, which must be unique
     * within the scope of a {@link UserDatabase}
     * @return
     */
    String getRolename();

    /**
     * Set the role name of this role, which must be unique
     * within the scope of a {@link UserDatabase}
     * @param rolename
     */
    void setRolename(String rolename);

    /**
     *
     * @return the {@link UserDatabase} within which this Role is defined
     */
    UserDatabase getUserDatabase();
}
