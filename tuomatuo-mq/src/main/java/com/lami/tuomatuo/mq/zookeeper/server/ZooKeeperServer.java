package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a simple standalone ZooKeeperServer. It sets up the
 * following chain of RequestProcessors to process request:
 * PrepRequestProcessor -> SyncRequestProcessor -> FinalRequestProcessor
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServer implements SessionTracker.SessionExpirer, ServerStats.Provider {

    protected static final Logger LOG;

    static {
        LOG = LoggerFactory.getLogger(ZooKeeperServer.class);
    }


    public static final int DEFAULT_TICK_TIME = 3000;



    public int getClientPort(){
        return 0;
    }


    @Override
    public long getOutstandingRequests() {
        return 0;
    }

    @Override
    public long getLastProcessedZxid() {
        return 0;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public int getNumAliveConnections() {
        return 0;
    }

    @Override
    public long getDataDirSize() {
        return 0;
    }

    @Override
    public long getLogDirSize() {
        return 0;
    }

    @Override
    public void expire(SessionTracker.Session session) {

    }

    @Override
    public long getServerId() {
        return 0;
    }

    public enum State {
        INITIAL, RUNNING, SHUTDOWN, ERROR;
    }

}