package javax.servlet.http;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * The interface used by a {@link HttpUpgradeHandler} to interact with an upgraded
 * HTTP connection
 * Created by xjk on 3/5/17.
 */
public interface WebConnection extends AutoCloseable{

    /**
     * Provides access to the {@link ServletInputStream} for reading data from
     * the client
     * @return
     * @throws IOException
     */
    ServletInputStream getInputStream() throws IOException;

    /**
     * Provides access to the {@link ServletOutputStream} for writing data to
     * the client
     * @return
     * @throws IOException
     */
    ServletOutputStream getOutputStream() throws IOException;

}
