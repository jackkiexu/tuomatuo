package javax.servlet;

import java.util.EventListener;

/**
 * Implementations of this interface receive notifications about changes to the
 * servlet context of the web application they are part of. To receive
 * notification events, the implementation class must be configured in the
 * deployment descriptor for the web application
 *
 * Created by xjk on 3/3/17.
 */
public interface ServletContextListener extends EventListener {

    /**
     * Notification that web application initializaton process is starting.
     * All ServletContextListeners are notified of context initialization before
     * any filter or servlet in the web application is initialized
     * @param sce
     */
    void contextInitialized(ServletContextEvent sce);

    /**
     * Notification that servlet context is about to be shut down. All
     * servlets and filters have been destroy()ed before any
     * ServletContextListeners are notified of context destruction.
     * @param sce
     */
    void contextDestroyed(ServletContextEvent sce);
}
