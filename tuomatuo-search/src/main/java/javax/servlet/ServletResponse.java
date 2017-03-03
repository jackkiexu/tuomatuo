package javax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Defines an object to assist a servlet in sending a response to the client
 * The servlet container creates a <code>ServletResponse</code> object and
 * passes it as an argument to the servlet's <code>service</code> method
 *
 * <p>
 *     To send binary data in a MIME body response, use the
 *     {@link ServletOutputStream} returned by {@link #getOutputStream}, To send
 *     character data, use the <code>PrintWriter</code> object returned by
 *     {@link #getWriter}. To mix binary and text data, for example, to create a
 *     multipart response, use a <code>ServletOutputStream</code> and manage the
 *     character sections manually
 * </p>
 *
 * The charset for the MIME body response can be specified explicitly using the
 * {@link #setCharacterEncoding} and {@link #setContentType} methods, or
 * implicitly using the {@link #setLocale} method. Explicit specifications take
 * precedence over implicit specifications. If no charset is specified,
 * ISO-8859-1 will be used. The <code>setCharacterEncoding</code>
 * <code>setContentType</code>, or <cpde>setLocale</cpde> method must be called
 * before <code>getWriter</code> and before committing the response for the
 * character encoding to be used
 *
 * See the Internet RFCs such as RFC 2045
 * for more information on MIME. Protocols such as SMTP and HTTP
 * define profiles of MIME, and those standards are still evolving.
 *
 * Created by xjk on 3/2/17.
 */
public interface ServletResponse {

    /**
     * Returns the name of the character encoding (MIME charset) used for the
     * body sent in this response. The character encoding may have been
     * specified explicitly using the {@link #setCharacterEncoding} or
     * {@link #setContentType} methods, or implicitly using the
     * {@link #setLocale} method. Explicit specifications take precedence over
     * implicit specifications. Calls made to these methods after
     * <code>getWriter</code> has been called or after the response has been
     * committed have no effect on the character encoding. If no charater
     * encoding has been specified <code>ISO-8859-1</code> is returned
     *
     * See RFC 2047 for more information
     * about character encoding and MIME
     * @return
     */
    String getCharacterEncoding();

    /**
     * Returns the content type used for the MIME body sent in this response
     * The content type proper must have been specified using
     * {@link #setContentType} before the response is committed. If no content
     * type has been specified, this method returns null. If a content type has
     * been specified and a character encoding has been explicitly or implicitly
     * specified as described in {@link #getCharacterEncoding()}, the charset
     * parameter is included in the string returned. If no character encoding
     * has been specified, the charset parameter is omitted.
     *
     * @return a <code>String</code> specifying the content type, for example,
     *      <code>text/html; charset=UTF-8</code>, or null
     */
    String getContentType();

    /**
     * Returns a {@link ServletOutputStream} suitable for writing binary data in
     *  the response. The servlet container does not encode the binary data.
     * <p>
     *     Calling flush() on the ServletOutputStream commits the response. Either
     *     this method or {@link #getWriter} may be called to write the body, not
     *     both
     * </p>
     *
     * @return
     * @throws IOException
     */
    ServletOutputStream getOutputStream() throws IOException;

    /**
     * Returns a <code>PrintWriter</code> object that can send character text to
     * the client. The <code>PrintWriter</code> uses the character encoding
     * returned by {@link #getCharacterEncoding()}, If the response's character
     * encoding has not been specified as described in
     * <code>getCharacterEncoding</code> (the method just returns the default
     * value ISO-8859-1), <code>getWriter</code> updates it
     * to <code>ISO-8859-1</code>
     * Calling flush() on the <code>PrintWriter</code> commits the response
     * Either this method or {@link #getOutputStream()} may be called to write the body, not both
     *
     * @return a PrintWriter object that can return character data to the client
     * @throws IOException
     */
    PrintWriter getWriter() throws IOException;

    /**
     * Sets the character encoding (MIME charset) of the response being sent to
     * the client, for example, to UTF-8. If the character encoding has already
     * been set by {@link #setContentType} or {@link #setLocale}, this method
     * overrides it. Calling {@link #setContentType} with the
     * <code>String</code> of <code>text/html</code> and calling this method
     * with the <code>String</code> of <code>UTF-8</code> is equivalent with
     * calling <code>setContentType</code> with the <code>String</code> of
     * <code>text/html; charset=UTF-8</code>
     *
     * This method can be called repeatedly to change the character encoding.
     * This method has no effect if it is called after <code>getWriter</code>
     * has been called or after the response has been committed.
     *
     * Containers must communicate the character encoding used for the servlet
     * response'writer
     * to the client if the protocol provides a way for doing
     * so. In the case of HTTP, the character encoding is communicated as part
     * of the <code>Content-Type</code> header for text media types. Note that
     * the character encoding cannot be communicated via HTTP headers if the
     * servlet does not specify a content type; however, it is still used to
     * encode text written via the servlet response's writer
     * @param charset
     */
    void setCharacterEncoding(String charset);

    /**
     * Sets the length of the content body in the response in HTTP servlets,
     * this method sets the HTTP Content-Length headeer
     *
     * @param len an integer specifying the length of the content being returned
     *            to the client; sets the Content-Length header
     */
    void setContentLength(int len);

    /**
     * Sets the length of the content body in the response In HTTP servlets,
     * this method sets the HTTP Content-Length header.
     *
     * @param length
     *            an integer specifying the length of the content being returned
     *            to the client; sets the Content-Length header
     *
     * @since Servlet 3.1
     */
    void setContentLengthLong(long length);


    /**
     * Sets the content type of the response being sent to the client, if the response has been committed yet.
     * The given content type may include a character encoding specification, for example
     * <code>text/html;charset=UTF-8</code>. The response's character encoding
     * is only set from the given content type if this method is called efore
     * <code>getWriter</code> is called
     *
     * This method may be called repeatedly to change content type and character
     * encoding. This method has no effect if called after the response has been
     * committed. It does not set the response's character encoding if it is
     * called after <code>getWriter</code> has been called or after the response
     * has been committed
     *
     * Container must communicate the content type and the character encoding
     * used for the servlet response's writer to the client if the protocol
     * provides a way for doing so. In the case of HTTP, the <code>Content-Type</code>
     * header is used
     *
     * @param type
     */
    void setContentType(String type);

    /**
     * Sets the preferred buffer size for the body of the response. The servlet
     * container will use a buffer at least as large as the size requested. The
     * actual buffer size used can be found using <code>getBufferSize</code>
     *
     * A larger buffer allow more content to be written before anything is
     * actually sent, thus providing the servlet with more time to set
     * appropriate status codes and headers, A smaller buffer decreases server
     * memory load and allows the client to start receiving data more quickly
     * This method must be called before any response body content is written.
     * if content has been written or the response object has been committed,
     * the method throws an <code>IllegalStateException</code>
     *
     * @param size
     */
    void setBufferSize(int size);

    /**
     * Returns the actual used for the response, If no buffering is
     * used, this method returns 0;
     * @return
     */
    int getBufferSize();

    /**
     * Force any content in the buffer to be written to  the client, A call to
     * this method automatically commits the response, meaning the status code
     * and headers will be written
     * @throws IOException
     */
    void flushBuffer() throws IOException;

    /**
     * Clears the content of the underfying buffer in the response without
     * clearing headers or status code. If the response has been committed,
     * this method throw an <code>IllegalStateException</code>
     */
    void resetBuffer();

    /**
     * Returns a boolean indicating if the response has been committed, A
     * committed response has already had its status code and headers written
     * @return
     */
    boolean isCommitted();

    /**
     * Clears any data that exist in the buffer as well as the status code and
     * headers, if the response has been committed, this method throws an
     * <code>IllegalStateException</code>
     */
    void reset();

    /**
     * Sets the locale of the response, if the response has not been committed
     * yet. It also sets the response's character encoding appropriately for the
     * locale, if the character encoding has not been explicitly set using
     * {@link #setContentType} or {@link #setCharacterEncoding},
     * <code>getWriter</code> han't been called yet, and the response hasn't
     * been committed yet. If the deployment descriptor contains a
     * <code>locale-encoding-mapping-list</code> element, and the element
     * provides a mapping for the given locale, that mapping is used. Otherwise
     * the mapping from locale to character encoding is container dependent.
     *
     * This method may be called repeatedly to change locale and character
     * encoding. The method has no effect if called after the response has been
     * committed. It does not set the response's character encoding if it is
     * called after {@link #setContentType} has been called with a charset
     * specification, after {@link #setCharacterEncoding(String)} has been called, after
     * <code>getWriter</code> has been called, or after the response has been
     * committed
     *
     * <p>
     *     Containers must communicate the locale and the character encoding used
     *     for the servlet response's writer to the client if the protocol provides
     *     a way for doing so. In the case of HTTP, the locale is communicated via
     *     the <code>Content-Language</code> header, the character encoding as part
     *     of the <code>Content-Type</code> header for the text media types. Note that
     *     the character encoding cannot be communicated via HTTP header if the
     *     servlet does not specify a content type; however, it is still used to encode text written via servlet response's writer
     * </p>
     *
     * @param loc
     */
    void setLocale(Locale loc);

    /**
     * Returns the locale specified for this response using the
     * {@link #setLocale} method. Calls made to <code>setLocale</code> after the
     * response is committed have no effect
     *
     * @return The locale specified for this response using the
     *      {@link #setLocale} method, If no locale has been specified,
     *      container's default locale is returned
     */
    Locale getLocale();
}
