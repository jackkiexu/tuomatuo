package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.*;

/**
 * A watch report, essentially a mapping of session ID to paths that the session
 * has set a watch on. This class is immutable
 * Created by xjk on 3/18/17.
 */
public class WatchesReport {

    private final Map<Long, Set<String>> id2paths;

    /**
     * Creates a new report
     *
     * @param id2paths map of session IDs to paths that each session has set a watch on
     */
    public WatchesReport(Map<Long, Set<String>> id2paths) {
        this.id2paths = Collections.unmodifiableMap(id2paths);
    }

    /**
     * @param m
     * @return
     */
    private static Map<Long, Set<String>> deepCopy(Map<Long, Set<String>> m){
        Map<Long, Set<String>> m2 = new HashMap<>();
        for(Map.Entry<Long, Set<String>> e : m.entrySet()){
            m2.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        return m2;
    }

    /**
     * Checks if the given session has watches set
     *
     * @param sessionId session ID
     * @return true if session has paths with watches set
     */
    public boolean hasPaths(long sessionId){
        return id2paths.containsKey(sessionId);
    }

    /**
     * Gets the paths that the given session has set watches on. The returned
     * set is immutable
     * @param sessionId session ID
     * @return paths that have watches set by the session, or null if none
     */
    public Set<String> getPaths(long sessionId){
        Set<String> s = id2paths.get(sessionId);
        return s != null ? Collections.unmodifiableSet(s) : null;
    }

    /**
     * Converts this report to map. The returned map is mutable, and changes
     * to it do not reflect back into this report
     * @return
     */
    public Map<Long, Set<String>> toMap() {
        return deepCopy(id2paths);
    }
}
