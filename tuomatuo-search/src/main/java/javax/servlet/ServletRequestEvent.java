package javax.servlet;

import java.util.EventObject;

/**
 * Events of this kind indicate lifecycle events for a ServletRequest. The
 * source of the event is the ServletContext of this web application
 *
 * Created by xjk on 3/5/17.
 */
public class ServletRequestEvent extends EventObject {
    private static final long serialVersionUID = -4674946849207477160L;

    private final transient ServletRequest request;

    /**
     * Constructs a ServletRequestEvent for the given ServletContext and
     * ServletRequest
     *
     * @throws IllegalArgumentException if source is null.
     */
    public ServletRequestEvent(ServletContext sc, ServletRequest request) {
        super(sc);
        this.request = request;
    }


    /**
     * Get the associated ServletRequest.
     * @return the ServletRequest that is changing.
     */
    public ServletRequest getServletRequest() {
        return this.request;
    }

    /**
     * Get the associated ServletContext.
     * @return the ServletContext that is changing.
     */
    public ServletContext getServletContext() {
        return (ServletContext) super.getSource();
    }
}
