package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Thrown when a {@link Leader} has too many concurrent snapshots being sent
 * to observers
 *
 * Created by xujiankang on 2017/3/19.
 */
public class SnapshotThrottleException extends Exception {

    private static final long serialVersionUID = 1L;

    public SnapshotThrottleException(int concurrentSnapshotNumber, int throttleThreshold) {
        super(getMessage(concurrentSnapshotNumber, throttleThreshold));
    }

    private static String getMessage(int concurrentSnapshotNumber, int throttleThreshold){
        return String.format("new snapshot would make %d concurrently in process." + " maximum is %d", concurrentSnapshotNumber, throttleThreshold);
    }

}
