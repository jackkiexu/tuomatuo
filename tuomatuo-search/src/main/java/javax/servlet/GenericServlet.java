package javax.servlet;

import java.io.Serializable;

/**
 * Defines a generic, protocol-independent servlet. To write an HTTP servlet for
 * use on the Web, extend {@link javax.servlet.http.HttpServlet} instead
 *
 * <code>GenericServlet</code> implements the <code>Servlet</code> and
 * <code>ServletConfig</code> interfaces. <code>GenericServlet</code> may be
 * directly extended by a servlet, although it's more common to extend a
 * protocol-specific subclass such as <code>HttpServlet</code>
 *
 * <code>GenericServlet</code> makes writting servlets easier. It provides simple
 * versions of the lifecycle methods <code>init</code> and <code>destroy</code>
 * and of the methods in the <code>ServletConfig</code> interface
 * <code>GenericServlet</code> also implements the <code>log</code> method,
 * declared in the <code>ServletContext</code> interface
 *
 * To write a generic servlet, you need override the abstract
 * <code>service</code> method
 *
 * Created by xjk on 3/4/17.
 */
public abstract class GenericServlet implements Servlet, ServletConfig, Serializable{

    private static final long serialVersionUID = 5676080717428382940L;

    private transient ServletConfig config;

    // Does nothing, All of the servlet initialization is done by one of the
    // <code>init</code> methods
    public GenericServlet() {
    }

}
