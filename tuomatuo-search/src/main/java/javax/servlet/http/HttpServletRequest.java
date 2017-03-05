package javax.servlet.http;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Extends the {@link ServletRequest} interface to provide request
 * information for HTTP servlets
 * The servlet container creates an <code>HttpServletRequest</code> object and
 * passes it as an argument to the servlet's service methods
 * doGet, doPost
 *
 * Created by xjk on 3/5/17.
 */
public interface HttpServletRequest extends ServletRequest {

    public static final String BASIC_AUTH = "BASIC";

    public static final String FORM_AUTH = "FORM";

    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";

    public static final String DIGEST_AUTH = "DIGEST";


    String getAuthType();

    Cookie[] getCookies();

    long getDateHeader(String name);

    String getHeader(String name);

    Enumeration<String> getHeaders(String name);

    Enumeration<String> getHeaderNames();

    int getIntHeader(String name);

    String getMethod();

    String getPathInfo();

    String getPathTranslated();

    String getContextPath();

    String getQueryString();

    String getRemoteUser();

    boolean isUserInRole(String role);

    Principal getUserPrincipal();

    String getRequestedSessionId();

    String getRequestURI();

    StringBuffer getRequestURL();

    String getServletPath();

    HttpSession getSession(boolean create);

    HttpSession getSession();

    String changeSessionId();

    boolean isRequestedSessionIdValid();

    boolean isRequestedSessionIdFromCookie();

    boolean isRequestedSessionIdFromURL();

    boolean isRequestedSessionIdFromUrl();

    boolean authenticate(HttpServletResponse response) throws IOException, ServletException;


    void login(String username, String password) throws ServletException;

    void logout() throws ServletException;

    Collection<Part> getParts() throws IOException, ServletException;

    Part getPart(String name) throws IOException, ServletException;

    <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException;
}
