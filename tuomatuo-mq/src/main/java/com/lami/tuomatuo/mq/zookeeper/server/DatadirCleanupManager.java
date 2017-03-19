package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * this class manages the cleanup of snapshots and corresponding transaction
 * logs by scheduling the auto purge task with the specified
 * 'autopurge.purgeInterval'. It keeps the most recent
 * 'autopurge.snapRetainCount' number of snapshots and corresponding transaction
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DatadirCleanupManager {
}
