package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/**
 * Receives notification of read events when using non-blocking IO
 * Created by xjk on 3/4/17.
 */
public interface ReadListener extends EventListener {

    /**
     * Invoked when data is available to read. The container will invoke this
     * method the first time for a request as soon as there is data to read
     * Subsequent invocation will only occur if a call to
     * ServletInputStream#isReady has returned false and data has
     * subsequently become available to read
     * @throws IOException
     */
    public abstract void onDataAvailable() throws IOException;

    /**
     * Invoked when the request body has been fully read
     * @throws IOException
     */
    public abstract void onAllDataRead() throws IOException;

    /**
     * INvoked if an error occurs while reading the request body
     * @param throwable
     */
    public abstract void onError(Throwable throwable);

}
