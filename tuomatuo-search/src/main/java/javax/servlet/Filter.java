package javax.servlet;

import java.io.IOException;

/**
 * A filter is an object that performs filtering tasks on either the request to
 * a resource(a servlet or static content), or on the response from a resource,
 * or both
 * Filters perform filtering in the <code>doFilter</code> method. Every Filter
 * has access to a FilterConfig object from which it can obtain its
 * initialization parameters, a reference to the ServletContext which it can
 * use, for example, to load resource needed for filtering tasks
 *
 * Filters are configured in the deployment descriptor of a web application
 *
 * Example that have been identified for this design are
 * 1) Authentication Filter
 * 2) Logging and Auditing Filters
 * 3) Image conversion Filters
 * 4) Data compression Filters
 * 5) Encryption Filters
 * 6) Tokenizing Filters
 * 7) Filters that trigger resource access events
 * 8) XSL/T filter
 * 9) MIME-TYPE chain Filter
 *
 * Created by xjk on 3/4/17.
 */
public interface Filter {

    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work
     * The we container cannot place the filter into service if the init method
     * either
     * Throws a ServletException
     * Does not return within a time period defined by the web
     * container
     *
     * @param filterConfig
     * @throws ServletException
     */
    void init(FilterConfig filterConfig) throws ServletException;

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due to a
     * client request for a resource at the end of the chain. The FilterChain
     * passed in to this method allows the Filter to pass on the request and
     * response to the next entity in the chain
     *
     * A typical implementation of this method would follow the following
     * pattern:
     * 1. Examine the request
     * 2. Optionally wrap the request object with a custom implementation to
     *      filter content or headers for input filtering
     * 3. Optionally wrap the response object with a custom implementation to
     *      filter content or headers for output filtering
     * 4. Either invoke the next entity in the chain using
     *      the FilterChain object <code>chain.doFilter</code>
     * 4. or not pass on the request/response pair to the
     *      next entity in the filter chain to block the request processing
     * 5. Directly set headers on the response after invocation of the next
     *      entity in the filter chain
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    void doFilter(ServletRequest request, ServletResponse response,
                  FilterChain chain) throws IOException, ServletException;


    /**
     * Called by the web container to indicateto a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter
     *
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory
     */
    void destroy();
}
