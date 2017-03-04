package javax.servlet;

import java.util.Set;

/**
 * ServletContainerInitializers are registered via an entry in the
 * file META-INF/services/ServletContainerInitializer that must be
 * included in the JAR file that contain the SCI implementation
 *
 * SCI processing is performed regardless of the setting of metadata-complete
 * SCI processing can be controlled per JAR file via fragment ordering. If an
 * absolute ordering is defined, the only those JARs included in the ordering
 * will be processed for SCIs. To disable SCI processing completely, an empty
 * absolute ordering may be defined
 *
 * SCIs register an interest in annotations (class, method or field) and/or
 * types via the HandlesTypes annotation which
 * is added to the class
 *
 * Created by xjk on 3/3/17.
 */
public interface ServletContainerInitializer {

    /**
     * Receives notification during startup of a web aplication of the classes
     * within the web application that matched the critera defined via the
     * HandlesTypes annotation
     * @param c
     * @param ctx
     * @throws ServletException
     */
    void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException;
}
