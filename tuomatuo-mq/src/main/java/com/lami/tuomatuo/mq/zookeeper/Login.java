package com.lami.tuomatuo.mq.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.CallbackHandler;

/**
 * This class is responsible for refreshing Kerberos credentials for
 * logins for both ZooKeeper client client and server
 * Created by xujiankang on 2017/3/19.
 */
public class Login {
    private static final Logger LOG = LoggerFactory.getLogger(Login.class);

    public CallbackHandler callbackHandler;

    /**
     * LoginThread will sleep until 80% of time from last refresh to
     * ticket's expiry has been reached, at which time it will wake
     * and try ro renew the ticket
     */
    public static final float TICKET_RENEW_WINDOW = 0.80f;

    private static final float TICKET_RENEW_JITTER = 0.05f;



}
