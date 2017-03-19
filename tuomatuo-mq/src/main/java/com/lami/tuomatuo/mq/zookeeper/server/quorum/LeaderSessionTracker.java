package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * The leader session tracker trackers local and global sessions on the leader
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderSessionTracker extends UpgradeableSessionTracker {
    @Override
    public long createSession(int sessionTimeout) {
        return 0;
    }

    @Override
    public boolean addGlobalSession(long id, int to) {
        return false;
    }
}
