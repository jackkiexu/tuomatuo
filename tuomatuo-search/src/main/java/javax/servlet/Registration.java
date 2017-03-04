package javax.servlet;

import java.util.Map;
import java.util.Set;

/**
 * Common interface for registration of Filters and Servlets
 *
 * Created by xjk on 3/3/17.
 */
public interface Registration {

    String getName();

    String getClassName();

    /**
     * Add an initialisation parameter if not already added
     * @param name
     * @param value
     * @return
     */
    boolean setInitParameter(String name, String value);

    /**
     * Get the value of an initialisation parameter
     *
     * @param name
     * @return
     */
    String getInitParameter(String name);

    /**
     * Add multiple initialisation parameters, If any of supplied
     * initialisation parameter conficts with an existing initialisation
     * parameter, no updates will be performed
     *
     * @param initParameters
     * @return
     */
    Set<String> setInitParameter(Map<String, String> initParameters);

    /**
     * Get the names and values of all the initialisation parameters.
     * @return
     */
    Map<String, String> getInitParameters();

    public interface Dynamic extends Registration{
        /**
         * Mark this Servlet/Filter as supported asynchronous processing
         * @param isAsyncSupported
         */
        public void setAsyncSupported(boolean isAsyncSupported);
    }
}
