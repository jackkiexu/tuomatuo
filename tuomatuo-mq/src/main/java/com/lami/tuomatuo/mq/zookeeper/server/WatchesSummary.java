package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A summary of watch information. This class is immutable
 *
 * Created by xjk on 3/18/17.
 */
public class WatchesSummary {

    /**
     * The key in the map returned by toMap for the number of
     * connections
     */
    public static final String KEY_NUM_CONNECTIONS = "num_connections";

    /**
     * The key in the map returned by toMap() for the number of paths
     */
    public static final String KEY_NUM_PATHS = "num_paths";

    /**
     * The key in the map returned by toMap() for the total number of
     * watches
     */
    public static final String KEY_NUM_TOTAL_WATCHES = "num_total_watches";

    private final int numConnections;
    private final int numPaths;
    private final int totalWatches;

    /**
     * Creates a new summary
     *
     * @param numConnections the number of sessions that have set watches
     * @param numPaths       the number of paths that have watches set on them
     * @param totalWatches   the total of watches set
     */
    public WatchesSummary(int numConnections, int numPaths, int totalWatches) {
        this.numConnections = numConnections;
        this.numPaths = numPaths;
        this.totalWatches = totalWatches;
    }

    /**
     * Gets the number of connections (sessions) that have set watches.
     * @return
     */
    public int getNumConnections() {
        return numConnections;
    }

    /**
     * Gets the number of path that have watches set on them
     * @return
     */
    public int getNumPaths() {
        return numPaths;
    }

    /**
     * Gets the total number of watches set
     * @return
     */
    public int getTotalWatches() {
        return totalWatches;
    }

    /**
     * Converts this summary to a map. The returned map is mutable, and changes
     * to it do not reflect back into this summary
     * @return
     */
    public Map<String, Object> toMap(){
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put(KEY_NUM_CONNECTIONS, numConnections);
        summary.put(KEY_NUM_PATHS, numPaths);
        summary.put(KEY_NUM_TOTAL_WATCHES, totalWatches);
        return summary;
    }
}
