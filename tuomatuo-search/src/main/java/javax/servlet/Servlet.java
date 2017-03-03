package javax.servlet;

/**
 * Defines methods that all servlets must implement
 *
 * <p>
 *     A servlet is a small Java program that runs within a web server. Servlets
 *     receive and respond to requests from Web client, usually across HTTP, the
 *     HyperText Transfer Protocol
 * </p>
 *
 * <p>
 *     To implement this interface, you can write a generic servlet that extends
 *     GenericServlet or an HTTP servlet that extends
 *     HttpServlet
 * </p>
 *
 * <p>
 *      This interface defines methods to initialize a servlet, to service requests,
 *      and to remove a servlet from the server. These are known as life-cycle
 *      methods and are called in the following sequence
 * </p>
 *
 * <li>
 *     The servlet is constructed, then initialized with the init
 *     method
 *     Any calls from clients to the service method are handled
 *     The servlet is constr
 * </li>
 *
 *
 *
 * Created by xujiankang on 2017/3/3.
 */
public interface Servlet {
}
