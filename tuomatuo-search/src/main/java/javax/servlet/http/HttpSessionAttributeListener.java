package javax.servlet.http;

/**
 * This listener interface can be implemented in order to get notifications of
 * changes to the attribute lists of sessions within this web applications
 * Created by xjk on 3/5/17.
 */
public interface HttpSessionAttributeListener {

    /**
     * Notification that an attribute has been replaced in a session. Called
     * after the attribute is added
     * @param se
     */
    void attributeAdded(HttpSessionBindingEvent se);

    /**
     * Notification that an attribute has been replaced in a session. Called
     * after the attribute is removed
     * @param se
     */
    void attributeRemoved(HttpSessionBindingEvent se);

    /**
     * Notification that an attribute has been replaced in a session. Called
     * after the attribute is replaced
     * @param se
     */
    void attributeReplaced(HttpSessionBindingEvent se);
}
