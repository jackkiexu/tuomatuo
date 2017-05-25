package com.lami.tuomatuo.mq.zookeeper.server.persistence;


import com.lami.tuomatuo.mq.zookeeper.server.DataTree;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * snapshot interface for the persistence layer
 * implement this interface for implementing
 * snapshots
 *
 * Created by xjk on 3/18/17.
 */
public interface SnapShot {

    /**
     * deserialize a data tree from the last valid snapshot and
     * return the last zxid that was deserialized
     *
     * @param dt the datatree to be deserialized into
     * @param sessions the sessions to be deserialized into
     * @return the last zxid that was deserialized from the snapshot
     * @throws IOException
     */
    long deserialize(DataTree dt, Map<Long, Integer> sessions) throws IOException;

    /**
     * Persist the datatree and the sessions into a persistence storage
     *
     * @param dt the datatree to be serialized
     * @param sessions
     * @param name
     * @throws IOException
     */
    void serialize(DataTree dt, Map<Long, Integer> sessions, File name) throws IOException;

    /**
     * find the most recent snapshot file
     *
     * @return the most recent snapshotfile
     * @throws IOException
     */
    File findMostRecentSnapshot() throws IOException;

    /**
     * free resource from this snapshot immediately
     * @throws IOException
     */
    void close() throws IOException;
}
