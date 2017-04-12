package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.SessionTracker;
import com.lami.tuomatuo.mq.zookeeper.server.SessionTrackerImpl;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is really just a shell of a SessionTracker that tracks session activity
 * to be forwarded to Leader using PING
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LearnerSessionTracker extends SessionTracker {

    public SessionExpirer expirer;

    public HashMap<Long, Integer> touchTable = new HashMap<>();
    public long serverId = 1;
    public long nextSessionId = 0;

    public ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;

    public LearnerSessionTracker(SessionExpirer expirer, ConcurrentHashMap<Long, Integer> sessionsWithTimeouts, long id) {
        this.expirer = expirer;
        this.sessionsWithTimeouts = sessionsWithTimeouts;
        this.serverId = id;
        nextSessionId = SessionTrackerImpl.initializeNextSession(this.serverId);
    }
}
