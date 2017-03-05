package javax.servlet;

/**
 * This is the event class for notifications of changes to the attributes of the
 * servlet request in an application
 *
 * Created by xjk on 3/5/17.
 */
public class ServletRequestAttributeEvent extends ServletRequestEvent {
    private static final long serialVersionUID = 7796045336490040572L;

    private final String name;
    private final Object value;


    /**
     * Construct a ServletRequestAttributeEvent giving the servlet context of
     * this web application, the ServletRequest whose attributes are changing
     * and the name and the value of the attribute
     * @param sc
     * @param request
     * @param name
     * @param value
     */
    public ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value) {
        super(sc, request);
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the attribute that changed on the ServletRequest
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the attribute that has been added, removed or
     * replaced. If the attribute was added, this is the value of the attribute.
     * If the attribute was removed, this is the value of the removed attribute.
     * If the attribute was replaced, this is the old value of the attribute
     * @return
     */
    public Object getValue() {
        return value;
    }
}
