package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * The learner session tracker is used by learners (followers and observers) to
 * track zookeeper sessions which may or may not be echoed to the leader. when
 * a new session is created it is saved locally in a wrapped
 * LocalSessionTracker. It can subsequently be upgraded to a global session
 * as required. If an upgrade is requested the session is removed from local
 * collections while keeping the same session ID. It is up to the caller to
 * queue a session creation request for the leadeer
 * A secondary function of the learner session tracker is to remember session
 * which have been touched in this service. This information is passed along
 * to the leader with a pin
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LearnerSessionTracker extends UpgradeableSessionTracker {

    @Override
    public long createSession(int sessionTimeout) {
        return 0;
    }

    @Override
    public boolean addGlobalSession(long id, int to) {
        return false;
    }
}
