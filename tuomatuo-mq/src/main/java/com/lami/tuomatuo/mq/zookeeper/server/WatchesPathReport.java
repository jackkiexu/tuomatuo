package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.*;

/**
 * A watch report, essentially a mapping of path to session IDs of sessions that
 * have set a watch on that path. This class is immutable
 * Created by xjk on 3/18/17.
 */
public class WatchesPathReport {

    private final Map<String, Set<Long>> path2Ids;

    /**
     * Creates a new report
     *
     * @param path2Ids map of paths to session IDs of sessions that have set a
     *                 watch on that path
     */
    public WatchesPathReport(Map<String, Set<Long>> path2Ids) {
        this.path2Ids = path2Ids;
    }

    private static Map<String, Set<Long>> deepCopy(Map<String, Set<Long>> m) {
        Map<String, Set<Long>> m2 = new HashMap<String, Set<Long>>();
        for (Map.Entry<String, Set<Long>> e : m.entrySet()) {
            m2.put(e.getKey(), new HashSet<Long>(e.getValue()));
        }
        return m2;
    }

    /**
     * Checks if the given path has watches set
     *
     * @param path path
     * @return true if path has watch set
     */
    public boolean hasSessions(String path){
        return path2Ids.containsKey(path);
    }

    /**
     * Gets the session IDs of sessions that have set watches on the given path
     * The returned set is immutable
     *
     * @param path session ID
     * @return session IDs of sessions that have set watches on the path, or
     *          null if none
     */
    public Set<Long> getSessions(String path){
        Set<Long> s = path2Ids.get(path);
        return s != null ? Collections.unmodifiableSet(s) : null;
    }

    /**
     * Converts this report to a map. The returned map is mutable, and changes
     * to it do not reflect back into this report
     * @return
     */
    public Map<String, Set<Long>> toMap() {
        return deepCopy(path2Ids);
    }
}
