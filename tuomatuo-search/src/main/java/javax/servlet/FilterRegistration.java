package javax.servlet;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created by xjk on 3/4/17.
 */
public interface FilterRegistration extends Registration{

    /**
     * Add a mapping for this filter to one or more named Servlets
     * @param dispatcherTypes The dispatch types to which this filter should apply
     * @param isMatchAfter Should this filter be applied after any mappings defined in the deployment descriptor
     * @param servletNames Request mapped to these servlets will be processed by this filter
     */
    void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames);

    Collection<String> getServletNameMappings();

    /**
     * Add a mapping for this filter to one or more URL patterns
     *
     * @param dispatcherTypes The dispatch types to which this filter should apply
     * @param isMatchAfter Should this filter be applied after any mappings defined in the deployment descriptor
     * @param urlPatterns The URL patterns to which this filter should be applied
     */
    void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns);

    Collection<String> getUrlPatternMappings();

    static interface Dynamic extends FilterRegistration, Registration.Dynamic{

    }
}
