package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 *This is a full featured SessionTracker. It tacker session in grouped by tick
 * interval. It always rounds up the tick interval to provide a sort of grace
 * period. Sessions are thus expired in the batches made up of sessions that expire in a given interval
 * Created by xujiankang on 2017/3/19.
 */
public class SessionTrackerImpl extends Thread implements SessionTracker {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTrackerImpl.class);


    @Override
    public long createSession(int sessionTimeout) {
        return 0;
    }

    @Override
    public void addSession(long id, int to) {

    }

    @Override
    public boolean touchSession(long sessionId, int sessionTimeout) {
        return false;
    }

    @Override
    public void setSessionClosing(long sessionId) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void removeSession(long sessionId) {

    }

    @Override
    public void checkSession(long sessionId, Object owner) throws KeeperException.SessionExpiredException {

    }

    @Override
    public void setOwner(long id, Object owner) throws KeeperException.SessionExpiredException {

    }

    @Override
    public void dumpSessions(PrintWriter pwriter) {

    }

    @Override
    public boolean addGlobalSession(long id, int to) {
        return false;
    }
}
