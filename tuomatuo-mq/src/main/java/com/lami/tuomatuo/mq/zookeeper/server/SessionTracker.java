package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;

import java.io.PrintWriter;

/**
 * This is the basic interface that ZooKeeperServer uses to track sessions. The
 * standalone and leader ZooKeeperServer use the same SessionTracker. The
 * FollowerZooKeeperServer uses a SessionTracker which is basically a simple
 * shell to track information to be forwarded to the leader
 *
 * Created by xujiankang on 2017/3/16.
 */
public interface SessionTracker {

    public static interface Session {
        long getSessionId();
        int getTimeout();
        boolean isClosing();
    }

    public static interface SessionExpirer {
        void expire(Session  session);
        long getServerId();
    }

    long createSession(int sessionTimeout);

    void addSession(long id, int to);

    /**
     * @return false if session is no longer active
     */
    boolean touchSession(long sessionId, int sessionTimeout);

    /**
     * Mark that the session is in the process of closing
     */
    void setSessionClosing(long sessionId);

    void shutdown();

    void removeSession(long sessionId);

    void checkSession(long sessionId, Object owner) throws KeeperException.SessionExpiredException;

    void setOwner(long id, Object owner) throws KeeperException.SessionExpiredException;

    /**
     * Text dump of session information, suitable for debugging
     * @param pwriter
     */
    void dumpSessions(PrintWriter pwriter);

    /**
     * Add a global session to these being tracked
     * @param id sessionId
     * @param to sessionTimeout
     * @return whether the session was newly added (if false, already existed)
     */
    boolean addGlobalSession(long id, int to);




}
