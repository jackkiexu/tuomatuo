package com.apache.catalina;

import com.apache.catalina.connector.Request;
import com.apache.catalina.connector.Response;
import com.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.ietf.jgss.GSSContext;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;

/**
 * A <b>Realm</b> is ready-only facade for an underlying security realm
 * used to authenticate individual users, and identify the security roles
 * associated with those users. Realms can be attached at any Container
 * level. but will typically only be attached to a Context. or higher level
 * Container
 *
 * Created by xjk on 3/6/17.
 */
public interface Realm {


    // ------------------------------------------------------------- Properties

    /**
     * @return the Container with which this Realm has been associated.
     */
    public Container getContainer();


    /**
     * Set the Container with which this Realm has been associated.
     *
     * @param container The associated Container
     */
    public void setContainer(Container container);


    /**
     * @return the CredentialHandler configured for this Realm.
     */
    public CredentialHandler getCredentialHandler();

    /**
     * Set the CredentialHandler to be used by this Realm.
     *
     * @param credentialHandler the {@link CredentialHandler} to use
     */
    public void setCredentialHandler(CredentialHandler credentialHandler);


    // --------------------------------------------------------- Public Methods

    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);


    /**
     * Try to authenticate with the specified username.
     *
     * @param username Username of the Principal to look up
     * @return the associated principal, or <code>null</code> if none is
     *         associated.
     */
    public Principal authenticate(String username);


    /**
     * Try to authenticate using the specified username and
     * credentials.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     * authenticating this username
     * @return the associated principal, or <code>null</code> if there is none
     */
    public Principal authenticate(String username, String credentials);


    /**
     * Try to authenticate with the specified username, which
     * matches the digest calculated using the given parameters using the
     * method described in RFC 2617 (which is a superset of RFC 2069).
     *
     * @param username Username of the Principal to look up
     * @param digest Digest which has been submitted by the client
     * @param nonce Unique (or supposedly unique) token which has been used
     * for this request
     * @param nc the nonce counter
     * @param cnonce the client chosen nonce
     * @param qop the "quality of protection" (<code>nc</code> and <code>cnonce</code>
     *        will only be used, if <code>qop</code> is not <code>null</code>).
     * @param realm Realm name
     * @param md5a2 Second MD5 digest used to calculate the digest :
     * MD5(Method + ":" + uri)
     * @return the associated principal, or <code>null</code> if there is none.
     */
    public Principal authenticate(String username, String digest,
                                  String nonce, String nc, String cnonce,
                                  String qop, String realm,
                                  String md5a2);


    /**
     * Try to authenticate using a {@link GSSContext}
     *
     * @param gssContext The gssContext processed by the {@link Authenticator}.
     * @param storeCreds Should the realm attempt to store the delegated
     *                   credentials in the returned Principal?
     * @return the associated principal, or <code>null</code> if there is none
     */
    public Principal authenticate(GSSContext gssContext, boolean storeCreds);


    /**
     * Try to authenticate using {@link X509Certificate}
     * @param certs
     * @return
     */
    Principal authenticate(X509Certificate certs[]);

    /**
     * Execute a periodic task, such as reloading, etc. This method will be
     * invoked inside the classloading context of this container. Unexpected
     * throwables will be caught and logged.
     */
    void backgroundProcess();

    /**
     * Find the SecurityConstraints onfigured to gurad the request URI for
     * this request
     *
     * @param request
     * @param context
     * @return
     */
    SecurityConstraint[] findSecurityConstraints(Request request, Context context);

    /**
     * Perform access control based on the specified authorization constraint.
     * @param request
     * @param response
     * @param constraint
     * @param context
     * @return <code>true</code> if this constraint is satisfied and processing
     *          should continue, or <code>false</code> otherwise
     * @throws IOException
     */
    boolean hasResourcePermission(Request request,
                                  Response response,
                                  SecurityConstraint[] constraint,
                                  Context context) throws IOException;

    /**
     * Check if the specified Principal has the specified
     * security role. within the context of this Realm
     * @param wrapper
     * @param principal
     * @param role
     * @return <code>true</code> if the specified Principal has the specified
     *          security role. within the context of this Realm; otherwise return
     *          <code>false</code>
     */
    boolean hasRole(Wrapper wrapper, Principal principal, String role);

    /**
     * Enfore any user data constraint required by the security constraint
     * guarding this request URI
     * @param request
     * @param response
     * @param constraints
     * @return <code>true</code> if this constraint
     *          was not violated and processing should continue, or <code>false</code>
     *          if we have created a response already
     */
    boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] constraints);

    /**
     * Remove a property change listener from this component
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
