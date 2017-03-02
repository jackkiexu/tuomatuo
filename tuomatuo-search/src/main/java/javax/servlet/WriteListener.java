package javax.servlet;

import java.io.IOException;

/**
 * Receives notification of write events when using non-blocking IO
 *
 * @since Servlet 3.1
 * Created by xjk on 3/2/17.
 */
public interface WriteListener {

    /**
     * Invoked when it is possible to write data with blocking. The containe
     * will invoke this method the first time for a request as soon as data can
     * be written. Subsequent invocations will only occur if a call to
     * {@link ServletOutputStream#"isReady()} has returned false and it has since
     * become possible to write data
     *
     * @throws IOException
     */
    void onWritePossible() throws IOException;

    /**
     * Invoked if an error occurs while writing the response
     *
     * @param throwable The throwable that represents the error that occurred
     */
    void onError(Throwable throwable);

}
