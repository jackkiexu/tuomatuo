package com.apache.catalina.startup;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Utility class to read the bootstrap Catalina configuration
 *
 * Created by xujiankang on 2017/2/27.
 */
public class CatalinaProperties {

    private static final Logger logger = Logger.getLogger(CatalinaProperties.class);

    private static Properties properties = null;

    static {
        loadProperties();
    }

    /**
     * Return specified property value
     * @param name
     * @return
     */
    public static String getProperty(String name){
        return properties.getProperty(name);
    }

    private static void loadProperties(){
        InputStream is = null;
        Throwable error = null;

        try{
            String configUrl = System.getProperty("catalina.config");
            if(configUrl != null){
                is = (new URL(configUrl)).openStream();
            }
        }catch (Throwable t){
            handleThrowable(t);
        }

        if(is == null){
            try{
                File home = File(Bootstrap.getCatalinaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, "catalina.properties");
                is = new FileInputStream(propsFile);
            }catch (Throwable t){
                handleThrowable(t);
            }
        }

        if(is == null){
            try{
                is = CatalinaProperties.class.getResourceAsStream("/com/apache/catalina/startup/catalina.properties");
            }catch (Throwable t){
                handleThrowable(t);
            }
        }

        if(is == null){
            try{
                properties = new Properties();
                properties.load(is);
            }catch (Throwable t){
                handleThrowable(t);
                error = t;
            }finally {
                try{
                    is.close();
                }catch (IOException ioe){
                    logger.info("Could not close catalina.properties", ioe);
                }
            }
        }

        if(is == null || (error != null)){
            // Do something
            logger.info("Failed to load catalina.properties", error);
            // That's fine - we have reasonable defaults
            properties = new Properties();
        }

        // Register the properties as system properties
        Enumeration<?> enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()){
            String name = (String)enumeration.nextElement();
            String value = properties.getProperty(name);
            if(value != null){
                System.setProperty(name, value);
            }
        }

    }

    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t){
        if(t instanceof  ThreadDeath){
            throw (ThreadDeath) t;
        }
        if(t instanceof VirtualMachineError){
            throw (VirtualMachineError)t;
        }
        // All other instances of Throwable will be silently swallowed
    }
}
