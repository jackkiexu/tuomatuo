package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a full featured SessionTracker. It tracks session in grouped by tick
 * interval. It always rounds up the tick interval to provide a sort of grace
 * period. Sessions are thus expired in batches made up of sessions that expire
 * in a given interval.
 */
public class SessionTrackerImpl extends Thread implements SessionTracker {
    private static final Logger LOG = LoggerFactory.getLogger(SessionTrackerImpl.class);
    // sessionId 与 Session 的映射关系
    HashMap<Long, SessionImpl> sessionsById = new HashMap<Long, SessionImpl>();

    // 过期时间 一组即将要处理的 session
    /**
     * 真正执行过期检查时, 会通过一个线程, 每隔开一段时间 sleep 一会, 然后 通过下面的时间线来从 sessionSets 里面进行获取对应的需要 remove 的 SessionSet
     * ----- SessionSet 1 -- SessionSet 2 -- SessionSet 3 ----
     * -------- time 1 ------ time 2 ------ time 3 ------
     */
    HashMap<Long, SessionSet> sessionSets = new HashMap<Long, SessionSet>();

    // sessionId 对应 timeout 时间 (这个对象真正放在 ZKDatabase 里面)
    ConcurrentHashMap<Long, Integer> sessionsWithTimeout;

    // 根据 sid 计算出的下一个新建的 sessionId
    long nextSessionId = 0;

    // 表示下次过期时间, 线程会在 nextExpirationTime时间来批量处理 session
    long nextExpirationTime;

    int expirationInterval;

    public static class SessionImpl implements Session {
        SessionImpl(long sessionId, int timeout, long expireTime) {
            this.sessionId = sessionId;
            this.timeout = timeout;
            this.tickTime = expireTime;
            isClosing = false;
        }

        final long sessionId;
        final int timeout;
        long tickTime;
        boolean isClosing;

        Object owner;

        public long getSessionId() { return sessionId; }
        public int getTimeout() { return timeout; }
        public boolean isClosing() { return isClosing; }
    }

    // 根据 QuorumPeer 的 myid 来生成 sessionId
    public static long initializeNextSession(long id) {
        long nextSid = 0;                                      // long 是 64 位
        nextSid = (System.currentTimeMillis() << 24) >>> 8;     // 将当前时间戳的最前面 8 为至0, 此时末尾的 16 也是0
        nextSid =  nextSid | (id <<56);                         // 将 myid 的数据补到 前面的时间戳上, 从这里也可以看出, myid 只是使用其最低的 8 位, 也就是 127, 超过了 127 个节点就可能出现 不同 QuorumPeer 的 SessionId 相同
        return nextSid;                                        // 将 初始 sessionId 返回
    }

    static class SessionSet {
        HashSet<SessionImpl> sessions = new HashSet<SessionImpl>();
    }

    com.lami.tuomatuo.mq.zookeeper.server.SessionTracker.SessionExpirer expirer;

    /**
     * 获取 session 的过期时间 (这里很多 session 的真正超时时间会一样, 并且会被安排在对应的 HashSet 里面)
     * @param time 这里的时间 time 是 : System.currentTimeMillis() + timeout
     * @return (time / 全局的 expirationInterval + 1) * 全局的 expirationInterval
     */
    private long roundToInterval(long time) {
        // We give a one interval grace period
        return (time / expirationInterval + 1) * expirationInterval;        // 计算这个 SessionImpl 的最近一个 超时时间(每个超时时间对应一个 Bucket)
    }

    public SessionTrackerImpl(com.lami.tuomatuo.mq.zookeeper.server.SessionTracker.SessionExpirer expirer,
                              ConcurrentHashMap<Long, Integer> sessionsWithTimeout, int tickTime,
                              long sid)
    {
        super("SessionTracker");
        this.expirer = expirer;
        this.expirationInterval = tickTime;
        this.sessionsWithTimeout = sessionsWithTimeout;
        nextExpirationTime = roundToInterval(System.currentTimeMillis());
        this.nextSessionId = initializeNextSession(sid);
        for (Map.Entry<Long, Integer> e : sessionsWithTimeout.entrySet()) {
            addSession(e.getKey(), e.getValue());
        }
    }

    volatile boolean running = true;

    volatile long currentTime;

    synchronized public void dumpSessions(PrintWriter pwriter) {
        pwriter.print("Session Sets (");
        pwriter.print(sessionSets.size());
        pwriter.println("):");
        ArrayList<Long> keys = new ArrayList<Long>(sessionSets.keySet());
        Collections.sort(keys);
        for (long time : keys) {
            pwriter.print(sessionSets.get(time).sessions.size());
            pwriter.print(" expire at ");
            pwriter.print(new Date(time));
            pwriter.println(":");
            for (SessionImpl s : sessionSets.get(time).sessions) {
                pwriter.print("\t0x");
                pwriter.println(Long.toHexString(s.sessionId));
            }
        }
    }


    @Override
    synchronized public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pwriter = new PrintWriter(sw);
        dumpSessions(pwriter);
        pwriter.flush();
        pwriter.close();
        return sw.toString();
    }

    @Override
    synchronized public void run() {
        try {
            while (running) {
                currentTime = System.currentTimeMillis();
                if (nextExpirationTime > currentTime) { // 1. 若下次的过期时间丹玉当前时间, 则进行 sleep
                    this.wait(nextExpirationTime - currentTime);
                    continue;
                }
                SessionSet set;
                set = sessionSets.remove(nextExpirationTime); // 移除将要删除的 session
                if (set != null) {
                    for (SessionImpl s : set.sessions) {
                        setSessionClosing(s.sessionId);         // 将 session 的状态至为 close
                        expirer.expire(s);                      // 给客户端提交 session 关闭的 request
                    }
                }
                nextExpirationTime += expirationInterval; // 加上过期间隔时间, 作为下次监测点的时间
            }
        } catch (InterruptedException e) {
            LOG.error("Unexpected interruption", e);
        }
        LOG.info("SessionTrackerImpl exited loop!");
    }

    // 更新 session 的过期时间
    synchronized public boolean touchSession(long sessionId, int timeout) {
        org.apache.zookeeper.server.ZooTrace.logTraceMessage(LOG,
                org.apache.zookeeper.server.ZooTrace.CLIENT_PING_TRACE_MASK,
                "SessionTrackerImpl --- Touch session: 0x"
                        + Long.toHexString(sessionId) + " with timeout " + timeout);

        SessionImpl s = sessionsById.get(sessionId);                            // 从 sessionsById 获取 session, sessionsById 是一个 SessionId <-> SessionImpl 的 map
        // Return false, if the session doesn't exists or marked as closing
        if (s == null || s.isClosing()) {
            return false;
        }
        long expireTime = roundToInterval(System.currentTimeMillis() + timeout); // 计算过期时间
        if (s.tickTime >= expireTime) {
            // Nothing needs to be done
            return true;
        }
        SessionSet set = sessionSets.get(s.tickTime);                         // 这里的 SessionSet 就是一个 timeout 对应额 Bucket, 将有一个线程, 在超时时间点检查这个 SessionSet
        if (set != null) {
            set.sessions.remove(s);
        }
        s.tickTime = expireTime;                                               // 下面的步骤就是将 session 以 tickTime 为单位放入 sessionSets 中
        set = sessionSets.get(s.tickTime);
        if (set == null) {
            set = new SessionSet();
            sessionSets.put(expireTime, set);
        }
        set.sessions.add(s);                                                   // 将 SessionImpl 放入对应的 SessionSets 里面
        return true;
    }

    synchronized public void setSessionClosing(long sessionId) {
        if (LOG.isTraceEnabled()) {
            LOG.info("Session closing: 0x" + Long.toHexString(sessionId));
        }
        SessionImpl s = sessionsById.get(sessionId);
        if (s == null) {
            return;
        }
        s.isClosing = true;
    }

    synchronized public void removeSession(long sessionId) {
        SessionImpl s = sessionsById.remove(sessionId);
        sessionsWithTimeout.remove(sessionId);
        if (LOG.isTraceEnabled()) {
            org.apache.zookeeper.server.ZooTrace.logTraceMessage(LOG, org.apache.zookeeper.server.ZooTrace.SESSION_TRACE_MASK,
                    "SessionTrackerImpl --- Removing session 0x"
                            + Long.toHexString(sessionId));
        }
        if (s != null) {
            SessionSet set = sessionSets.get(s.tickTime);
            // Session expiration has been removing the sessions
            if(set != null){
                set.sessions.remove(s);
            }
        }
    }

    public void shutdown() {
        LOG.info("Shutting down");

        running = false;
        if (LOG.isTraceEnabled()) {
            org.apache.zookeeper.server.ZooTrace.logTraceMessage(LOG, org.apache.zookeeper.server.ZooTrace.getTextTraceLevel(),
                    "Shutdown SessionTrackerImpl!");
        }
    }


    synchronized public long createSession(int sessionTimeout) {
        addSession(nextSessionId, sessionTimeout);
        return nextSessionId++;
    }

    synchronized public void addSession(long id, int sessionTimeout) {              // 这里的 sessionid 在集群中每个 QuorumPeer 上都不会出现相同, 详情见 (SessionTrackerImpl.initializeNextSession)
        sessionsWithTimeout.put(id, sessionTimeout);                                  // 一个普通的 sessionId <-> sessionTimeout, 但其会在 takeSnapshot 时进行持久化
        if (sessionsById.get(id) == null) {                                           // 因为可能有重复请求, 所以加上这个判断
            SessionImpl s = new SessionImpl(id, sessionTimeout, 0);                      // 构建 session
            sessionsById.put(id, s);                                                   // 放入一个 sessionId <-> SessionImpl 的 Map 中去
            org.apache.zookeeper.server.ZooTrace.logTraceMessage(LOG, org.apache.zookeeper.server.ZooTrace.SESSION_TRACE_MASK,
                    "SessionTrackerImpl --- Adding session 0x"
                            + Long.toHexString(id) + " " + sessionTimeout);
        } else {
            if (LOG.isTraceEnabled()) {
                org.apache.zookeeper.server.ZooTrace.logTraceMessage(LOG, ZooTrace.SESSION_TRACE_MASK,
                        "SessionTrackerImpl --- Existing session 0x"
                                + Long.toHexString(id) + " " + sessionTimeout);
            }
        }
        touchSession(id, sessionTimeout);
    }

    synchronized public void checkSession(long sessionId, Object owner) throws KeeperException.SessionExpiredException, KeeperException.SessionMovedException{
        SessionImpl session = sessionsById.get(sessionId);
        if (session == null || session.isClosing()) {
            throw new KeeperException.SessionExpiredException();
        }
        if (session.owner == null) {
            session.owner = owner;
        } else if (session.owner != owner) {
            throw new KeeperException.SessionMovedException();
        }
    }

    synchronized public void setOwner(long id, Object owner) throws KeeperException.SessionExpiredException {
        SessionImpl session = sessionsById.get(id);
        if (session == null || session.isClosing()) {
            throw new KeeperException.SessionExpiredException();
        }
        session.owner = owner;
    }
}
