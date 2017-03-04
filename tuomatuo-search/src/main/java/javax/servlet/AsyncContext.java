package javax.servlet;

/**
 * Created by xjk on 3/4/17.
 */
public interface AsyncContext {

    static final String ASYNC_REQUEST_URI = "javax.servlet.async.request_uri";
    static final String ASYNC_CONTEXT_PATH = "javax.servlet.async.context_path";
    static final String ASYNC_PATH_INFO = "javax.servlet.async.path_info";
    static final String ASYNC_SERVLET_PATH = "javax.servlet.async.servlet_path";
    static final String ASYNC_QUERY_STRING = "javax.servlet.async.query_string";

    ServletRequest getRequest();

    ServletResponse getResponse();

    boolean hasOriginalRequestAndResponse();

    void dispatch();

    /**
     *
     * @param path The path to which the request/response should be dispatched
     *             relative to the {@link ServletContext} from which this async
     *             request was started
     */
    void dispatch(String path);

    /**
     *
     * @param context The path to which the request/response should be dispatched
     *                relative to the specified {@link ServletContext}
     * @param path
     */
    void dispatch(ServletContext context, String path);

    void complete();

    void start(Runnable run);

    void addListener(AsyncListener listener);

    void addListener(AsyncListener listener, ServletRequest request, ServletResponse response);

    <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException;

    /**
     * Set the timeout
     * @param timeout
     */
    void setTimeout(long timeout);

    /**
     * Get the current
     * @return
     */
    long getTimeout();
}
