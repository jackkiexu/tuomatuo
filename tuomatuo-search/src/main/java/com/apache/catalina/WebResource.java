package com.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;

/**
 * Represent a file or directory within a web application. It borrows heavily
 * from File
 * Created by xjk on 3/6/17.
 */
public interface WebResource {

    long getLastModified();

    /**
     * Return the last modified time of this resource in the correct format for
     * the HTTP Last-Modified header as specified by RFC 2616
     * @return
     */
    String getLastModifiedHttp();

    /**
     * Indicates if this resource is required for applications to correctly scan
     * the file structure but that does not exist in either the main or any
     * additional WebResourceSet. For example, if an external
     * directly is mapped to /WEB_INF/lib in an otherwise empty web
     * application, /WEB_INF will be represented as a virtual resource
     *
     * @return
     */
    boolean exists();

    /**
     *
     * @return
     */
    boolean isVirtual();

    boolean isDirectory();

    boolean isFile();

    boolean delete();

    String getName();

    long getContentLength();

    String getCanonicalPath();

    boolean canRead();

    /**
     * The path of this resource relative to the web application root. If the
     * resource is a directory, the return value will end in '/'
     * @return
     */
    String getWebappPath();

    /**
     * Return the string ETag if available else return
     * the weak ETag calculated from the content length and last modified
     * @return
     */
    String getETag();


    /**
     * Set the MIME type for this Resource
     * @param mimeType
     */
    void setMimeType(String mimeType);

    /**
     * Get the MIME type for this Resource
     * @return
     */
    String getMimeType();

    /**
     * Obtain an InputStream based on the contents of this resource
     * @return An InputStream based on the contents of this resource or
     *          <code>null</code> if the resource does not exist or does not
     *          represent a file
     */
    InputStream getInputStream();

    /**
     * Obtain the cached binary content of this resource
     * @return
     */
    byte[] getContent();

    /**
     * The Time the file was created. If not available, the result of
     * {@link #getLastModified()} will be returned
     * @return
     */
    long getCreation();


    /**
     * Get code base for this resource that will be used when looking up the
     * assigned permissions for the code base in the security policy file when
     * running under a security manager.
     */
    URL getCodeBase();

    /**
     * Obtain a reference to the WebResourceRoot of which this WebResource is a
     * part.
     */
    WebResourceRoot getWebResourceRoot();

    /**
     * Obtain the certificates that were used to sign this resource to verify
     * it or @null if none.
     *
     * @see java.util.jar.JarEntry#getCertificates()
     */
    Certificate[] getCertificates();

    /**
     * Obtain the manifest associated with this resource or @null if none.
     *
     * @see java.util.jar.JarFile#getManifest()
     */
    Manifest getManifest();
}
