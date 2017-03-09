package com.apache.catalina.startup;

import com.apache.catalina.Globals;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by xujiankang on 2017/2/27.
 */
public class Bootstrap {

    private static final Logger logger = Logger.getLogger(Bootstrap.class);

    private static Bootstrap daemon = null;

    private static final File catalinaBaseFile;
    private static final File catalinaHomeFile;

    private static final Pattern PATH_PATTERN = Pattern.compile("(\".*?\")|(([^,])*)");

    static {
        // Will always be non-null
        String userDir = System.getProperty("user.dir");

        // Home first
        String home = System.getProperty(Globals.CATALINA_HOME_PROP);
        File homeFile = null;

        if(home != null){
            File f = new File(home);
            try{
                homeFile = f.getCanonicalFile();
            }catch (IOException ioe){
                homeFile = f.getAbsoluteFile();
            }
        }

        if(homeFile == null){
            // First fall-back See if current directory is a bin directory
            // in a normal Tomcat install
            File bootstrapJar = new File(userDir, "bootstrap.jar");

            if(bootstrapJar.exists()){
                File f = new File(userDir, "..");
                try{
                    homeFile = f.getCanonicalFile();
                }catch (IOException ioe){
                    homeFile = f.getAbsoluteFile();
                }
            }
        }

        if(homeFile == null){
            // Second fall-back. Use current directory
            File f = new File(userDir);
            try{
                homeFile = f.getCanonicalFile();
            }catch (IOException ioe){
                homeFile = f.getAbsoluteFile();
            }
        }

        catalinaHomeFile = homeFile;
        System.setProperty(Globals.CATALINA_HOME_PROP, catalinaHomeFile.getPath());
        // Then base
        String base = System.getProperty(Globals.CATALINA_BASE_PROP);
        if(base == null){
            catalinaBaseFile = catalinaHomeFile;
        }else{
            File baseFile = new File(base);
            try {
                baseFile = baseFile.getCanonicalFile();
            } catch (IOException e) {
                baseFile = baseFile.getAbsoluteFile();
            }
            catalinaBaseFile = baseFile;
        }
        System.setProperty(Globals.CATALINA_BASE_PROP, catalinaBaseFile.getPath());
    }

    // ---------------------------------------------------------------------- Variables

    /**
     * Daemon reference
     */
    private Object catalinaDaemon = null;

    ClassLoader commonLoader = null;
    ClassLoader catalinaLoader = null;
    ClassLoader sharedLoader = null;

    // ---------------------------------------------------------------

    private void initClassLoaders(){
        try{
            commonLoader = createClassLoader("common", null);
            if(commonLoader == null){
                // no config file, default to this loader - we might be in a 'single' env
                commonLoader = this.getClass().getClassLoader();
            }
            catalinaLoader = createClassLoader("server", commonLoader);
            sharedLoader = createClassLoader("shared", commonLoader);
        }catch (Throwable t){
            handleThrowable(t);
            logger.info("Class loader creation threw exception", t);
            System.exit(1);
        }
    }

    /**
     * Obtain the name of configured home (binary) directory. Note that home and
     * base may be the same (and are by default)
     * @return
     */
    public static String getCatalinaHome(){
        return catalinaHomeFile.getPath();
    }

    /**
     * Obtain the name of the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set
     * the value returned by {@link #getCatalinaBase()} will be used
     * @return
     */
    public static String getCatalinaBase(){
        return catalinaBaseFile.getPath();
    }

    /**
     * Obtain the configured home (binary) directory. Note that home and
     * base may be the same (and are by the default)
     * @return
     */
    public static File getCatalinaHomeFile(){
        return catalinaHomeFile;
    }

    /**
     * Obtain the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set the value returned by {@link #getCatalinaHomeFile()}
     * will be used
     *
     * @return
     */
    public static File getCatalinaBaseFile(){
        return catalinaBaseFile;
    }

    private ClassLoader createClassLoader(String name, ClassLoader parent) throws Exception{
        String value = CatalinaProperties.getProperty(name + ".loader");
        if(value == null || value.equals("")){
            return parent;
        }

        value = replace(value);

        List<ClassLoaderFactory.Repository> repositories =  new ArrayList<>();
        String[] repositoryPaths = null;

        for(String repository : repositoryPaths){
            // check for a JAR URL repository
            try{
                URL url = new URL(repository);
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.URL));
            }catch (MalformedURLException e){}

            // Local repository
            if(repository.endsWith("*.jar")){
                repository = repository.substring(0, repository.length() - "*.jar".length());
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.GLOB));
            }else if(repository.endsWith(".jar")){
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.JAR));
            }else{
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.DIR));
            }
        }

        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }

    /**
     * System property replacement in the given string
     *
     * @param str The original string
     * @return the modified string
     */
    protected String replace(String str){
        // Implementation is copied from ClassLoaderLogManager.replace(),
        // but added special processing for catalina home and catalina.base
        String result = str;
        int pos_start = str.indexOf("${");
        if(pos_start >= 0){
            StringBuilder buider = new StringBuilder();
            int pos_end = -1;
            while(pos_start >= 0){
                buider.append(str, pos_end + 1, pos_start);
                pos_end = str.indexOf('}', pos_start + 2);
                if(pos_end < 0){
                    pos_end = pos_start - 1;
                    break;
                }

                String propName = str.substring(pos_start + 2, pos_end);
                String replacement;
                if(propName.length() == 0){
                    replacement = null;
                }else if(Globals.CATALINA_HOME_PROP.equals(propName)){
                    replacement = getCatalinaHome();
                }else if(Globals.CATALINA_BASE_PROP.equals(propName)){
                    replacement = getCatalinaBase();
                }else{
                    replacement = System.getProperty(propName);
                }

                if(replacement != null){
                    buider.append(replacement);
                }else{
                    buider.append(str, pos_start, pos_end + 1);
                }

                pos_start = str.indexOf("${", pos_end + 1);
            }

            buider.append(str, pos_end + 1, str.length());
            result = buider.toString();
        }
        return result;
    }

    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t){
        if(t instanceof ThreadDeath){
            throw (ThreadDeath) t;
        }
        if(t instanceof VirtualMachineError){
            throw (VirtualMachineError)t;
        }
        // All other instances of Throwable will be silently swallowed
    }

}
