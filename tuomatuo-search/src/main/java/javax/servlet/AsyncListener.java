package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/**
 * Created by xjk on 3/4/17.
 */
public interface AsyncListener extends EventListener {
    void onComplete(AsyncEvent event) throws IOException;
    void onTimeout(AsyncEvent event) throws IOException;
    void onError(AsyncEvent event) throws IOException;
    void onStartAsync(AsyncEvent event) throws IOException;
}
