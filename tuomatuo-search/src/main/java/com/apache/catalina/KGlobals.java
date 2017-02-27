package com.apache.catalina;

/**
 * Global constants that are applicable to multiple package within Catalina
 *
 * Created by xujiankang on 2017/2/27.
 */
public final class KGlobals {

    /**
     * The servlet context attribute under which we store the alternate
     * deployment descriptor for this web application
     */
    public static final String ALT_DD_ATTR = "org.apache.catalina.deploy.alt_dd";

    /**
     * Name of the system property containing
     * the tomcat instance installation path
      */
    public static final String CATALINA_HOME_PROP = "catalina.home";

    /**
     * Name of the system property containing
     * the tomcat instance installation path
     */
    public static final String CATALINA_BASE_PROP = "catalina.base";
}
