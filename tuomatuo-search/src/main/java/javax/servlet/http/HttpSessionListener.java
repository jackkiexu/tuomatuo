package javax.servlet.http;

import java.util.EventListener;

/**
 * Implementations of this interface are notified of changes to the list of
 * active sessions in a web application. To receive notification events, the
 * implementation class must be configured in the deployment descriptor for the
 * we application
 *
 * Created by xjk on 3/5/17.
 */
public interface HttpSessionListener extends EventListener{

    /**
     * Notification that a session was created
     * @param se
     */
    void sessionCreated(HttpSessionEvent se);

    /**
     * Notification that a session is about to be invalidated
     * @param se
     */
    void sessionDestroyed(HttpSessionEvent se);

}
