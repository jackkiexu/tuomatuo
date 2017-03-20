package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * this class manages the cleanup of snapshots and corresponding transaction
 * logs by scheduling the auto purge task with the specified
 * 'autopurge.purgeInterval'. It keeps the most recent
 * 'autopurge.snapRetainCount' number of snapshots and corresponding transaction
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DatadirCleanupManager {

    private static final Logger LOG = LoggerFactory.getLogger(DatadirCleanupManager.class);

    /** Status of the dataDir purge task */
    public enum PurgeTaskStatus{
        NOT_STARTED, STARTED, COMPLETED;
    }

    private PurgeTaskStatus purgeTaskStatus = PurgeTaskStatus.NOT_STARTED;

    private final File snapDir;

    private final File dataLogDir;

    private final int snapRetainCount;

    private final int purgeInterval;

    private Timer timer;

    /**
     * Constructor of DatadirCleanManager. It takes the parameters to schedule
     * the purge task
     *
     * @param snapDir snapshot directory
     * @param dataLogDir transaction log director
     * @param snapRetainCount number of snapshots to be retained after purge
     * @param purgeInterval purge interval in hours
     */
    public DatadirCleanupManager(File snapDir,
                                 File dataLogDir,
                                 int snapRetainCount,
                                 int purgeInterval) {
        this.snapDir = snapDir;
        this.dataLogDir = dataLogDir;
        this.snapRetainCount = snapRetainCount;
        this.purgeInterval = purgeInterval;
        LOG.info("autopurge.snapRetainCount set to " + snapRetainCount);
        LOG.info("autopurge.purgeInterval set to " + purgeInterval);
    }


    static class PurgeTask extends TimerTask{
        private File logsDir;
        private File snapsDir;
        private int  snapRetainCount;

        public PurgeTask(File logsDir, File snapsDir, int snapRetainCount) {
            this.logsDir = logsDir;
            this.snapsDir = snapsDir;
            this.snapRetainCount = snapRetainCount;
        }


        @Override
        public void run() {
            LOG.info("Purge task started");

            try{
                PurgeTxnLog.purge(logsDir, snapsDir, snapRetainCount);
            }catch (Exception e){
                LOG.error("Error occurred while purging", e);
            }
            LOG.info("Purge task completed");
        }
    }


    /**
     * Returns the number of snapshots to be retained after purge
     * @return
     */
    public int getSnapRetainCount(){
        return snapRetainCount;
    }
}
