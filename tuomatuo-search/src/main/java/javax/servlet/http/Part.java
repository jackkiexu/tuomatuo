package javax.servlet.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * This class represents a part as uploaded to the server as part of a
 * <code>multipart/form-data</code> request body. The part may represent either
 * an uploaded file or form data
 *
 * Created by xjk on 3/5/17.
 */
public interface Part {

    /**
     * Obtain an <code>InputStream</code> that can be used to retrieve the
     * contents of the file
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Obtain the content type passed by the browser
     * @return
     */
    String getContentType();

    /**
     * Obtain the name of the field in the multipart form corresponding to this
     * part
     * @return
     */
    String getName();

    /**
     * If this part represents an uploaded file, gets the file name submitted
     * in the upload. Returns {@code null} if no file name ia available or if
     * this part is not a file upload
     * @return
     */
    String getSubmittedFileName();

    /**
     * Obtain the size of this part
     * @return
     */
    long getSize();

    /**
     * A convenience method to write an upload part to disk. The client code
     * is not concerned with whether or not the part is stored in memory, or on
     * disk in a temporary location. They just want to write the upload part
     * to a file
     *
     * This method is not guaranteed to succeed if called more than once for
     * the same part. This allows a particular implementation to use, for
     * example, file renaming. where possible, rather than copying all of the
     * underlying data, thus gaining a significant performance benefit
     *
     * @param fileName
     * @throws IOException
     */
    void write(String fileName) throws IOException;

    /**
     * Deletes the underlying storage for a part, including deleting any
     * associated temporary disk file. Although the container will delete this
     * storage automatically this method can be ensure that this is done
     * at an earlier time, thus preserving system resources
     * Containers are only required to delete the associated storage when the
     * Part instance is garbage collected. Apache Tomcat will delete the
     * associated storage when the associated request has finished processing
     * Behaviour of other containers may be different
     *
     * @throws IOException
     */
    void delete() throws IOException;

    /**
     * Obtains the value of the specified part headers as a String. If there are
     * nultiple headers with the same name, this method returns the first header
     * in the part. The header name is case insensitive
     * @param name
     * @return The header value <code>null</code> if the header is not
     *          present
     */
    String getHeader(String name);

    /**
     * Obtain all the values of the specified part header
     * @param name
     * @return All the values of the specified part header. If the part did not
     *          include any headers of the specified name, this method returns an
     *          empty Collection
     */
    Collection<String> getHeaders(String name);

    /**
     * Get the header names provided for this part
     * @return
     */
    Collection<String> getHeaderNames();

}
