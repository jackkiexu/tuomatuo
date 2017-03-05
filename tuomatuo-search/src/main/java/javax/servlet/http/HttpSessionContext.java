package javax.servlet.http;

import java.util.Enumeration;

/**
 * Do not use
 * As of java servlet API 2.1 for security reasons, with no
 *      replacement. This interface will be rmeoved in a future version
 *      of this API
 * Created by xjk on 3/5/17.
 */
public interface HttpSessionContext {


    HttpSession getSession(String sessionId);

    Enumeration<String> getIds();
}
