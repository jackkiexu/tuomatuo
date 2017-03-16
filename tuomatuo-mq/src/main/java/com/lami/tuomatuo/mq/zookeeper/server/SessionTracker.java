package com.lami.tuomatuo.mq.zookeeper.server;

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

    /**
     * Add a global session to these being tracked
     * @param id sessionId
     * @param to sessionTimeout
     * @return whether the session was newly added (if false, already existed)
     */
    boolean addGlobalSession(long id, int to);




}
