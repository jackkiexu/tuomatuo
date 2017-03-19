package com.lami.tuomatuo.mq.zookeeper.server;

/**
 *This is a full featured SessionTracker. It tacker session in grouped by tick
 * interval. It always rounds up the tick interval to provide a sort of grace
 * period. Sessions are thus expired in the batches made up of sessions that expire in a given interval
 * Created by xujiankang on 2017/3/19.
 */
public class SessionTrackerImpl extends ZooKeeperCriticalThread implements SessionTracker {

    public SessionTrackerImpl(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }

    @Override
    public long createSession(int sessionTimeout) {
        return 0;
    }

    @Override
    public boolean addGlobalSession(long id, int to) {
        return false;
    }
}
