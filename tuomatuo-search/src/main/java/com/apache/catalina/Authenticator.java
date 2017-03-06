package com.apache.catalina;

import com.apache.catalina.connector.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An <b>Authenticator</b> is a component (usually a Value or Container) that
 * provides some sort of authentication service
 * Created by xjk on 3/6/17.
 */
public interface Authenticator {

    /**
     * Authenticate the user marking this request, based on the login
     * configuration of the {@link Context} with which this Authenticator is
     * associated
     *
     * @param request Request we are processing
     * @param response Response we are populating
     * @return <code>true</code> if any specified constraints have been
     *      satisfied, or <code>false</code> if one more constraints were not
     *      satisfied (in which case an authentication challenge will have
     *      been written to response)
     * @throws IOException
     */
    boolean authenticate(Request request, HttpServletResponse response) throws IOException;

    void login(String userName, String password, Request request) throws ServletException;

    void logout(Request request);
}
