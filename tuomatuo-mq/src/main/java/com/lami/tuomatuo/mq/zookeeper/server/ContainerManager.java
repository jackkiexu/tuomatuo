package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * manages cleanup of container ZNodes. This class is meant to only
 * be run from the leader. There's no harm in running from followers/observers
 * but that will be extra work that's not needed, Once started, it periodically
 * checks container nodes that have a cversion > 0 and have no children, A
 * delete is attempted on the node. The result of the delete is unimportant
 * If the proposal fails or the container node is not empty there's no harm
 * Created by xujiankang on 2017/3/19.
 */
public class ContainerManager {

    private static final Logger logger = LoggerFactory.getLogger(ContainerManager.class);

    private final ZKDatabase zkDb;
    private final RequestProcessor requestProcessor;
    private final int checkIntervalMs;
    private final int maxPerMinute;
    private  Timer timer;

    private final AtomicReference<TimerTask> task = new AtomicReference<>();

    /**
     * @param zkDb the ZK Database
     * @param requestProcessor request processor - used to inject dalete container request
     * @param checkIntervalMs how often to check container in milliseconds
     * @param maxPerMinute the max container to delete per second - avoid herding of container deletion
     */
    public ContainerManager(ZKDatabase zkDb, RequestProcessor requestProcessor, int checkIntervalMs, int maxPerMinute) {
        this.zkDb = zkDb;
        this.requestProcessor = requestProcessor;
        this.checkIntervalMs = checkIntervalMs;
        this.maxPerMinute = maxPerMinute;
    }
}
