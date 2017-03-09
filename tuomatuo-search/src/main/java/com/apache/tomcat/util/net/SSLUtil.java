package com.apache.tomcat.util.net;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

/**
 * Created by xjk on 3/9/17.
 */
public interface SSLUtil {

    SSLContext createSSLContext() throws Exception;

    KeyManager[] getKeyManagers() throws Exception;

    TrustManager[] getTrustManagers() throws Exception;

    void configureSessionContext(SSLSessionContext sslSessionContext);

    /**
     * Determines the SSL cipher suites that can be enabled, based on the
     * configuration of the endpoint and the ciphers supported by the SSL
     * 
     * @param context
     * @return
     */
    String[] getEnableCiphers(SSLContext context);

    String[] getEnableProtocols(SSLContext context);


}
