package javax.servlet.http;

import java.util.EventListener;

/**
 * Objects that are bound to a session may listen to container events notifying
 * them that sessions will be be passivated and session will be activated. A
 * container that migrates session between VMs or persists sessions is required
 * HttpSessionActivationListener
 * Created by xjk on 3/5/17.
 */
public interface HttpSessionActivationListener extends EventListener {

    /**
     * Notification that the session is about to be passivated
     * @param se
     */
    void sessionWillPassivate(HttpSessionEvent se);

    /**
     * Notification that the session has just been activated
     * @param se
     */
    void sessionDidActivate(HttpSessionEvent se);

}
