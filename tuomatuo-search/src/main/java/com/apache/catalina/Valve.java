package com.apache.catalina;

import com.apache.catalina.comet.CometEvent;
import com.apache.catalina.connector.Request;
import com.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A <b>Value</b> is a request processing component associated with a
 * particular Container. A series of Value are generally associated with
 * each other into a Pipeline. The detailed contract for a Value is included
 * in the description of the <code>invoke()</code> method below
 *
 * The "Value" name was assigned to this concept
 * because a value is what you use in a real world pipeline to control and/or
 * modify flows through it
 *
 * Created by xjk on 3/6/17.
 */
public interface Valve {

    /**
     * Return the next Valve in the pipeline containing this value, if any
     * @return
     */
    Valve getNext();

    /**
     * Set the next Valve in the pipeline containing this valve
     * @param value
     */
    void setNext(Valve value);

    /**
     * Execute a periodic task, such as reloading, etc. This method will be
     * invoked inside the classloading context of this container. Unexpected
     * throwable will be caught and logged
     */
    void backgroundProcess();


    /**
     * Perform request processing as required by this Valve
     *
     * An individual Valve MAY perform the following actions, in
     * the specified order:
     *
     * Examine and/or midify the properties of the specified Request and
     * Response
     *
     * Examine the properties of the specified Request, completely generate
     * the corresponding Response, and return control to the caller
     *
     * Examine the properties of the specified Request and Response, wrap
     * either or both of these objects to supplement their functionality
     * and pass them on
     *
     * If the corresponding Response was not generated (and control was not
     * returned, call the next Valve in the pipline (if there is one ) by
     * executing <code>getNext.invoke</code>
     *
     * Examine, but not modify, the properties of the resulting Response
     * (which was created by a subsequently invoked Valve or Container)
     *
     * A Valve MUST NOT do any of the followig things
     *
     * Change request properties that have already been used to direct
     * the flow of processing control for this request (for instance,
     * trying to change the virtual host to which a Request should be
     * sent from a pipeline attached to a Host or Context in the
     * standard implementation)
     *
     * Create a completed Response <strong>AND</strong> pass this
     * Request and Response on to the nextValve in the pipeline
     *
     * Consume bytes from the input stream associated with the request
     * unless it is completely generating the response, or wrapping the
     * request before passing it on
     *
     * Modify the HTTP headers included with the Response after the
     * <code>getNext.invoke</code> method has returned
     *
     * Perform any actions on the output stream associated with the
     * specified Response after the <code>getNext().invoke</code> method has
     * returned
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    void invoke(Request request, Response response) throws IOException, ServletException;

    /**
     * Process a Comet event
     * @param request
     * @param response
     * @param event
     * @throws IOException
     * @throws ServletException
     */
    void event(Request request, Response response, CometEvent event) throws IOException, ServletException;

    boolean isAsyncSupported();
}
