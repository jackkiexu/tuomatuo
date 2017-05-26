package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.sasl.SaslServer;

/**
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperSaslServer {

    public static final String LOGIN_CONTEXT_NAME_KEY = "zookeeper.sasl.serverconfig";
    public static final String DEFAULT_LOGIN_CONTEXT_NAME = "Server";

    private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperSaslServer.class);

    public SaslServer saslServer;

    public ZooKeeperSaslServer(final Login login) {
        this.saslServer = saslServer;
    }

    public SaslServer createSaslServer(final Login login){
        return null;
    }


    public byte[] evaluateResponse(byte[] response) throws Exception{
        return saslServer.evaluateResponse(response);
    }

    public boolean isComplete(){
        return saslServer.isComplete();
    }

    public String getAuthorizationID(){
        return saslServer.getAuthorizationID();
    }
}
