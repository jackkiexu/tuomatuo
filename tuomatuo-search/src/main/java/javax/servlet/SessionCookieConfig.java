package javax.servlet;

/**
 * Configures the session cookies used by the we application associated with
 * the ServletContext from which this SessionCookieConfig was obtained
 *
 * @since Servlet 3.0
 * Created by xjk on 3/2/17.
 */
public interface SessionCookieConfig {

    /**
     * Sets the session cookie name
     * @param name the name of the session cookie
     */
    void setName(String name);

    String getName();

    /**
     * Sets the domain for the session cookie
     * @param domain The session cookie domain
     */
    void setDomain(String domain);

    String getDomain();

    /**
     * Sets the path of the session cookie
     * @param path
     */
    void setPath(String path);

    String getPath();

}
