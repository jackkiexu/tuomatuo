package com.lami.tuomatuo.mq.jafka.utils;

import com.sohu.jafka.utils.*;
import org.apache.log4j.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * If mx4j-tools is in the classpath call maybeLoad to load the HTTP interface of mx4j
 *
 * The default port is 8081. To override the provide e.g -Dmx4jport=8082
 * THe default listen address is 0.0.0.0. To override that provide -Dmx4jaddress=127.0.0.1
 *
 * Created by xjk on 10/4/16.
 */
public class Mx4jLoader {

    private static final Logger logger = Logger.getLogger(Mx4jLoader.class);

    /**
     * Starts a JMX over http interface if and mx4j-tools.jar is in the
     * classpath.
     *
     * @return true if successfully loaded.
     */
    public static boolean maybeLoad() {
        try {
            if (!com.lami.tuomatuo.mq.jafka.utils.Utils.getBoolean(System.getProperties(), "jafka_mx4jenable", false)) {
                return false;
            }
            logger.debug("Will try to load mx4j now, if it's in the classpath");
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName processorName = new ObjectName("Server:name=XSLTProcessor");

            Class<?> httpAdaptorClass = Class.forName("mx4j.tools.adaptor.http.HttpAdaptor");
            Object httpAdaptor = httpAdaptorClass.newInstance();
            httpAdaptorClass.getMethod("setHost", String.class).invoke(httpAdaptor, getAddress());
            httpAdaptorClass.getMethod("setPort", Integer.TYPE).invoke(httpAdaptor, getPort());

            ObjectName httpName = new ObjectName("system:name=http");
            mbs.registerMBean(httpAdaptor, httpName);

            Class<?> xsltProcessorClass = Class.forName("mx4j.tools.adaptor.http.XSLTProcessor");
            Object xsltProcessor = xsltProcessorClass.newInstance();
            httpAdaptorClass.getMethod("setProcessor", Class.forName("mx4j.tools.adaptor.http.ProcessorMBean")).invoke(httpAdaptor, xsltProcessor);
            mbs.registerMBean(xsltProcessor, processorName);
            httpAdaptorClass.getMethod("start").invoke(httpAdaptor);
            logger.info("mx4j successfuly loaded");
            return true;
        } catch (ClassNotFoundException e) {
            logger.info("Will not load MX4J, mx4j-tools.jar is not in the classpath");
        } catch (Exception e) {
            logger.warn("Could not start register mbean in JMX", e);
        }
        return false;
    }

    private static String getAddress() {
        return System.getProperty("mx4jaddress", "0.0.0.0");
    }

    private static int getPort() {
        return com.sohu.jafka.utils.Utils.getInt(System.getProperties(), "mx4jport", 8082);
    }

}
