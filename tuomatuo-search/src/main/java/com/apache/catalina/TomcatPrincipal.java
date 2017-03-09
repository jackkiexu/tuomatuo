package com.apache.catalina;

import org.ietf.jgss.GSSCredential;

import java.security.Principal;

/**
 * Created by xjk on 3/6/17.
 */
public interface TomcatPrincipal extends Principal {

    /**
     * The authenticated Principal to be exposed to applications.
     */
    Principal getUserPrincipal();

    /**
     * The user's delegated credentials.
     */
    GSSCredential getGssCredential();

    /**
     * Calls logout, if necessary, on any associated JAASLoginContext. May in
     * the future be extended to cover other logout requirements.
     *
     * @throws Exception If something goes wrong with the logout. Uses Exception
     *                   to allow for future expansion of this method to cover
     *                   other logout mechanisms that might throw a different
     *                   exception to LoginContext
     *
     */
    void logout() throws Exception;
}
