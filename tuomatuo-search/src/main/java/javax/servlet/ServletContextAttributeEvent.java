package javax.servlet;

/**
 * This is the event class for notifications about changes to the attributes of
 * the servlet context of a web applications
 *
 * Created by xjk on 3/4/17.
 */
public class ServletContextAttributeEvent extends ServletContextEvent {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Object value;

    /**
     * Construct a ServletContextAttributeEvent from the given context for the
     * given attribute name and attribute value.
     * @param source The ServletContext associated with this attribute event
     * @param name The name of the servlet context attribute
     * @param value the value of the servlet context attribute
     */
    public ServletContextAttributeEvent(ServletContext source, String name, Object value) {
        super(source);
        this.name = name;
        this.value = value;
    }

    /**
     * Return the name of attribute thatc changed on the ServletContext
     * @return
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns the value of the attribute that has been added, removed, or
     * replaced.
     * @return
     */
    public Object getValue(){
        return this.value;
    }
}
