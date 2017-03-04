package javax.servlet;

import java.util.EventObject;

/**
 * This is the event class for notifications about changes to the servlet
 * context of a web application
 *
 * Created by xjk on 3/4/17.
 */
public class ServletContextEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ServletContextEvent(Object source) {
        super(source);
    }

    /**
     * Return the ServletContext that changed.
     *
     * @return the ServletContext that sent the event.
     */
    public ServletContext getServletContext() {
        return (ServletContext) super.getSource();
    }
}
