package javax.servlet.http;

/**
 * Interface between the HTTP upgrade process and the new protocol
 *
 * Created by xjk on 3/5/17.
 */
public interface HttpUpgradeHandler {

    /**
     * This method is called once the request/response pair where
     * {@link HttpServletRequest} is called has completed
     * processing and is the point where control of the connection passes from
     * the container to the {@link HttpUpgradeHandler}
     * @param connection
     */
    void init(WebConnection connection);

    /**
     * This method is called after the upgraded connection has been closed
     */
    void destroy();
}
