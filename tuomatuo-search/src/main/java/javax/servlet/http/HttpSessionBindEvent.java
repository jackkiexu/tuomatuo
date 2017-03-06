package javax.servlet.http;

/**
 * Events of this type either sent to an object that implements
 * {@link HttpSessionBindingListener} when it is bound or unbound from a
 * session, to a {@link HttpSessionAttributeListener} that has been
 * configured in the deployment descriptor when any attribute is bound, unbound
 * or replaced is a session
 *
 * Created by xjk on 3/6/17.
 */
public class HttpSessionBindEvent extends HttpSessionEvent {

    private static final long serialVersionUID = -2241687343299424082L;

    /** The name to which the object is being bound or unbound */
    private final String name;
    /** The object is being bound or unbound */
    private final String value;


    /**
     * Construct an event that notifies an object that it has been bound to or
     * unbound from a session. To receive the vent, the object must implement
     * {@link HttpSessionBindingListener}
     *
     * @param source
     */
    public HttpSessionBindEvent(HttpSession source, String name) {
        super(source);
        this.name = name;
        this.value = null;
    }

    /**
     * Construct an event that notifies an object that it has been bound to or
     * unbound from a session. To receive the vent, the object must implement
     * {@link HttpSessionBindingListener}
     * @param source
     * @param name
     * @param value
     */
    public HttpSessionBindEvent(HttpSession source, String name, String value) {
        super(source);
        this.name = name;
        this.value = value;
    }

    /**
     * Get the session that changed
     * @return
     */
    @Override
    public HttpSession getSession() {
        return super.getSession();
    }

    /**
     * Returns the name with which the attribute is bound or unbound from the session
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the attribute that has been added, removed or replaced
     * @return If the attribute was added(or bound), this is the value of the
     *      attribute. If the attribute was removed (or unbound), this is the
     *      value of the removed attribute. If the attribute was replaced,
     *      this is the old value of the attribute
     */
    public String getValue() {
        return value;
    }
}
