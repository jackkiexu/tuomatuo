package javax.servlet.http;

import java.util.EventListener;

/**
 * Cause an object to be notified when it is bound to or unbound from a
 * session. The object is notified by an {@link HttpSessionBindingEvent} object.
 * This may be as a result of a servlet programer explicitly unbinding an
 * attribute from a session, due to a session being invalidated, or due to a
 * session timing out
 *
 * Created by xjk on 3/5/17.
 */
public interface HttpSessionBindingListener extends EventListener{

    /**
     * Notifies the object that it is being bound to a session and identifies
     * the session
     * @param event
     */
    void valueBound(HttpSessionBindingEvent event);

    /**
     * Notifies the object that identifies the session and identifies the session
     * @param event
     */
    void valueUnbound(HttpSessionBindingEvent event);

}
