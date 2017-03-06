package javax.servlet.http;

import java.util.EventObject;

/**
 * This is the class representing event notifications for changes to sessions
 * within a web application
 *
 * Created by xjk on 3/6/17.
 */
public class HttpSessionEvent extends EventObject {
    private static final long serialVersionUID = -8229673481795717378L;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public HttpSessionEvent(Object source) {
        super(source);
    }

    /**
     * Get the session that changed.
     *
     * @return The session that changed
     */
    public HttpSession getSession() {
        return (HttpSession) super.getSource();
    }
}
