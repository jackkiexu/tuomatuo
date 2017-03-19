package com.lami.tuomatuo.mq.zookeeper.server.admin;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;

/**
 * Interface for an embedded admin server that runs Commands. There is only one
 * functional implementation, JettyAdminServer, DummyAdminServer, which does
 * nothing is used when we do not wish to run a server
 * Created by xujiankang on 2017/3/19.
 */
public interface AdminServer {

    public void start() throws AdminServerException;
    public void shutdown() throws AdminServerException;
    public void setZooKeeperServer(ZooKeeperServer zkServer);

    public class AdminServerException extends Exception{
        private static final long serialVersionUID = 1L;

        public AdminServerException(String message, Throwable cause) {
            super(message, cause);
        }

        public AdminServerException(Throwable cause) {
            super(cause);
        }
    }

}
