package com.apache.catalina;

import java.util.EventObject;

/**
 * General event for notifying listeners of significant changes on a Session
 *
 * Created by xjk on 3/9/17.
 */
public final class SessionEvent extends EventObject{
    private static final long serialVersionUID = -5927282552664203947L;


    /**
     * The event data associated with this event
     */
    private final Object data;


    /**
     * The session on which this event occurred
     */
    private final Session session;

    /**
     * The event type this instance represents
     */
    private final String type;


    public SessionEvent(Object source, Object data, Session session, String type) {
        super(source);
        this.data = data;
        this.session = session;
        this.type = type;
    }



    /**
     * Return the event data of this event.
     */
    public Object getData() {

        return (this.data);

    }


    /**
     * Return the Session on which this event occurred.
     */
    public Session getSession() {

        return (this.session);

    }


    /**
     * Return the event type of this event.
     */
    public String getType() {

        return (this.type);

    }


    /**
     * Return a string representation of this event.
     */
    @Override
    public String toString() {

        return ("SessionEvent['" + getSession() + "','" +
                getType() + "']");

    }


}
