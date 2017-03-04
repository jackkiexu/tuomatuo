package javax.servlet;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides an input stream for readline binary data from a client request,
 * including an efficient <code>readLine</code> method for reading data one line
 * at a time. With some protocols, such as HTTP POST and PUT, a
 * <code>ServletInputStream</code> object can be used to read data sent from the
 * client
 *
 * A <code>ServletInputStream</code> object is normally retrieved via the
 * {@link ServletRequest#getInputStream()} method
 *
 * This is an abstract class that a servlet container implements. Subclasses of
 * this class must implement the InputStream.read() method
 *
 * Created by xjk on 3/4/17.
 */
public abstract class ServletInputStream extends InputStream{

    /**
     * Does nothing, because this is an abstract class
     */
    protected ServletInputStream() {
    }

    /**
     * Read the input stream, one line at a time, Starting at an offset, reads
     * bytes into an array, until it reads a certain number of bytes or reaches
     * a newline character, which it reads into the array as well
     * This method returns -s if it reaches the end of the input stream before
     * reading the maximum number of bytes
     *
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public int readLine(byte[] b, int off, int len) throws IOException{
        if(len <= 0) return 0;
        int count = 0, c;

        while((c = read()) != -1){
            b[off++] = (byte)c;
            count++;
            if(c == '\n' || count == len){
                break;
            }
        }
        return count > 0 ? count : -1;
    }

    /**
     * Has the end of this InputStream been reached
     * @return true if all the data has been read from the stream else false
     */
    public abstract boolean isFinished();

    /**
     * Can data be read from this InputStream without blocking?
     * Returns if this method is called and returns false, the container will
     * invoke {@link ReadListener#onDataAvailable()} when data is available
     *
     * @return true if data can be read without blocking, else false
     */
    public abstract boolean isReady();

    /**
     * Sets the {@link ReadListener} for this {@link ServletInputStream} and
     * thereby switches to non-blocking IO, It is only valid to switch to
     * non-blocking IO within async processing or HTTP upgrade processing
     *
     * @param listener the non-blocking IO read listener
     */
    public abstract void setReadListener(ReadListener listener);
}
