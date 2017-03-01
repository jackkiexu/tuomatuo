package com.apache.catalina.startup;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Created by xujiankang on 2017/2/27.
 */
public final class ClassLoaderFactory {

    private static final Logger logger = Logger.getLogger(ClassLoaderFactory.class);

    /**
     * Create and return a new class loader, based on the configuration
     * defaults and specified directory paths:
     *
     * @param unpacked Array of pathnames to unpacked directories that should
     *                 be added to the repositories of the class loader, or <code>null</code>
     *                 for no unpacked directories to be considered
     * @param packed Array of pathnames to directories containing JAR files
     *               that should be added to the repositories of the class loader,
     *               or <code>null</code> for no directories fo JAR files to be considered
     * @param parent Parentclass loader for the new class loader, or
     *               <code>null</code> for the system class loader
     * @return
     */
    public static ClassLoader createClassLoader(File unpacked[], File packed[], final ClassLoader parent) throws Exception{
        logger.info("Creating new class loader");
        // Construct the "class path" for this class loader

        Set<URL> set = new LinkedHashSet<>();

        // Add unpacked directories
        if(unpacked != null){
            for(int i = 0; i < packed.length; i++){
               File file = unpacked[i];
                if(!file.exists() || !file.canRead()){
                    continue;
                }
                file = new File(file.getCanonicalPath() + File.separator);
                URL url = file.toURI().toURL();
                logger.info(" Including directory " + url);
                set.add(url);
            }
        }

        // Add packed directory JAR files
        if(packed != null){
            for(int i = 0; i < unpacked.length; i++){
                File file = unpacked[i];

            }
        }
        return null;
    }


    /**
     * Create and return a new class loader, based on the configuration
     * default and the specified directory paths:
     *
     * @param repositories List of class directories, jar files, jar directories
     *                     or URLS that should be added to the repositories of
     *                     the class loader
     * @param parent Parent class loader for the new class loader, or
     *               <code>null</code> for the system class loader
     * @return
     * @throws Exception
     */
    public static ClassLoader createClassLoader(List<Repository> repositories, final ClassLoader parent) throws Exception{
        logger.info("Creating new class loader");

        // Construct the "class path" for this class loader
        Set<URL> set = new LinkedHashSet<>();

        if(repositories != null){
            for(Repository repository : repositories){
                if(repository.getType() == RepositoryType.URL){
                    URL url = buildClassLoaderUrl(repository.getLocation());
                    logger.info(" Including URL " + url);
                    set.add(url);
                }else if(repository.getType() == RepositoryType.DIR){
                    File directory = new File(repository.getLocation());
                    directory = directory.getCanonicalFile();
                    if(!validateFile(directory, RepositoryType.DIR)){
                        continue;
                    }

                    URL url = buildClassLoaderUrl(directory);
                    logger.info(" Including directory " + url);
                    set.add(url);
                }else if(repository.getType() == RepositoryType.JAR){
                    File file = new File(repository.getLocation());
                    file = file.getCanonicalFile();
                    if(!validateFile(file, RepositoryType.JAR)){
                        continue;
                    }
                    URL url = buildClassLoaderUrl(file);
                    logger.info(" Including jar file " + url);
                    set.add(url);
                }else if(repository.getType() == RepositoryType.GLOB){
                    File directory = new File(repository.getLocation());
                    directory = directory.getCanonicalFile();
                    if(!validateFile(directory, RepositoryType.GLOB)){
                        continue;
                    }
                    logger.info(" Including directory glob " + directory.getAbsolutePath());
                    String filenames[] = directory.list();
                    if(filenames == null){
                        continue;
                    }

                    for(int j = 0; j < filenames.length; j++){
                        String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                        if(!filename.endsWith(".jar")){
                            continue;
                        }
                        File file = new File(directory, filenames[j]);
                        file = file.getCanonicalFile();
                        if(!validateFile(file, RepositoryType.JAR)){
                            continue;
                        }
                        logger.info(" Including glob jar file " + file.getAbsolutePath());
                        URL url = buildClassLoaderUrl(file);
                        set.add(url);
                    }
                }
            }
        }

        // Construct the class loader itself
        final URL[] array = set.toArray(new URL[set.size()]);
        logger.info(" location :" + Arrays.toString(array));

       return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>(){

           @Override
           public URLClassLoader run() {
               if(parent == null){
                   return new URLClassLoader(array);
               }else {
                   return new URLClassLoader(array, parent);
               }
           }
       });
    }

    private static boolean validateFile(File file, RepositoryType type) throws IOException{
        if(RepositoryType.DIR == type || RepositoryType.GLOB == type){
            if(!file.exists() || !file.isDirectory() || !file.canRead()){
                String msg = " Problem with directory [" + file +
                        "], exists: [" + file.exists() +
                        "], isDirectory: [ " + file.isDirectory() +
                        "], canRead: [" + file.canRead() + "]";
                File home = new File(Bootstrap.getCatalinaHome());
                home = home.getCanonicalFile();
                File base = new File(Bootstrap.getCatalinaBase());
                base = base.getCanonicalFile();
                File defaultvalue = new File(base, "lib");

                /**
                 * existence of ${catalina base}/lib directory is optional
                 * Hide the warning if Tomcat runs with separate catalina home
                 * and catalina.base and the directory is absent
                 */
                if(!home.getPath().equals(base.getPath())
                        && file.getPath().equals(defaultvalue.getPath())
                        && !file.exists()){

                }
                logger.info(msg);
                return false;
            }
        }else if(RepositoryType.JAR == type){
            if(!file.exists() || !file.canRead()){
                logger.info("Problen with JAR file [" + file +
                        "], exists: [" + file.exists() +
                                "], canRead: [ " + file.canRead() + "]"
                );
                return false;
            }
        }
        return true;
    }

    /**
     * These two method would ideally be in the utility class
     * com.apache.tomcat.util.buf.UriUtil but that class is not visible until
     * after the class loaders have been constructed
     *
     * @param urlString
     * @return
     * @throws MalformedURLException
     */
    private static URL buildClassLoaderUrl(String urlString)throws MalformedURLException{
        // URLs passed to class loaders may point to directories that contain
        // JARs. if these URL are used to construct URLs for resources in a JAR
        // the URL will be used as is. It therefore necessary to ensure that
        // the sequence "!/" is not present in a class loader URL
        String result = urlString.replaceAll("!/", "%21/");
        return new URL(result);
    }

    private static URL buildClassLoaderUrl(File file) throws MalformedURLException{
        // Could be a directory or a file
        String fileUrlString = file.toURI().toString();
        fileUrlString = fileUrlString.replace("!/", "%21/");
        return new URL(fileUrlString);
    }


    public static enum RepositoryType{
        DIR,
        GLOB,
        JAR,
        URL
    }

    public static class Repository{
        private final String location;
        private final RepositoryType type;

        public Repository(String location, RepositoryType type) {
            this.location = location;
            this.type = type;
        }
        public String getLocation(){
            return location;
        }

        public RepositoryType getType(){
            return type;
        }
    }

}
