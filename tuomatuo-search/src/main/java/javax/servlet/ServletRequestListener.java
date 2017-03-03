package javax.servlet;

import java.util.EventListener;

/**
 * A ServletRequestListener can be implementation by the developer
 * interested in being notified of requests coming in and out of
 * scope in a web component, A request is defined as coming into
 * scope when it is about enter the first servlet or filter
 * in each web application, as going out of scope when it exits
 * the last servlet or the first filter in the chain
 *
 * Created by xujiankang on 2017/3/3.
 */
public interface ServletRequestListener extends EventListener {

    /**
     * The request is about to go out of scope the web application
     */
    void requestDestroyed(ServletRequestEvent sre);

    /**
     * The request is about to come into scope of the web application
     * @param sre
     */
    void requestInitialized(ServletRequestEvent sre);
}
