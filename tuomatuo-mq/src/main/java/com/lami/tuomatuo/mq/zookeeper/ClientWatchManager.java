package com.lami.tuomatuo.mq.zookeeper;

import java.util.Set;

/**
 * Created by xujiankang on 2017/3/16.
 */
public interface ClientWatchManager {

    /**
     * Return a set of watchers that should be notified of the event. The
     * manager must not notify the watcher(s), however it will update it's
     * internal structure as if the watches had triggered. The intent being
     * that the callee is now responsible for notifying the watchers of the
     * event, possibly at some later time
     *
     * @param state event state
     * @param type event type
     * @param path event path
     * @return may be empty but must not be null
     */
    public Set<Watcher> materialize(Watcher.Event.KeeperState state, Watcher.Event.EventType type, String path);
}
