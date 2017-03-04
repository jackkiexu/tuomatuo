package javax.servlet;

import java.io.IOException;

/**
 * A FilterChain is an object provided by the servlet container to the developer
 * giving a view into the invocation chain of a filtered request for a resource.
 * Filters use the FilterChain to invoke the next filter in the chain, or if the
 * calling filter is the last filter in the chain, to invoke the resource at the
 * end of the chain
 *
 * Created by xjk on 3/4/17.
 */
public interface FilterChain {

    /**
     * Causes the next filter in the chain to be invoke, or if the calling
     * filter is the last filter in the chain, causes the resource at the end of
     * the chain to be invoked
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException;
}
