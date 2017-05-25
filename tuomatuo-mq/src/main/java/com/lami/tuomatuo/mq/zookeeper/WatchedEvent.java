package com.lami.tuomatuo.mq.zookeeper;

import lombok.Data;
import org.apache.zookeeper.proto.WatcherEvent;

/**
 * A WatchedEvent represents a change on the ZooKeeper that a Watcher
 * is able to respond to. The WatchedEvent includes exactly what happened,
 * the current state of the ZooKeeper, and the path of the znode that
 * was involved in the event
 *
 * Created by xujiankang on 2017/3/19.
 */
public class WatchedEvent {

    final private Watcher.Event.KeeperState keeperState;
    final private Watcher.Event.EventType eventType;
    private String path;

    public WatchedEvent(Watcher.Event.EventType eventType,
                        Watcher.Event.KeeperState keeperState, String path) {
        this.keeperState = keeperState;
        this.eventType = eventType;
        this.path = path;
    }

    /**
     * Convert a WatcherEvent sent over the write into a full-fledged WatcherEvent
     * @param eventMessage
     */
    public WatchedEvent(WatcherEvent eventMessage) {
        keeperState = Watcher.Event.KeeperState.fromInt(eventMessage.getState());
        eventType = Watcher.Event.EventType.fromInt(eventMessage.getType());
        path = eventMessage.getPath();
    }


    public Watcher.Event.KeeperState getState() {
        return keeperState;
    }

    public Watcher.Event.EventType getType() {
        return eventType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public WatcherEvent getWrapper(){
        return new WatcherEvent(eventType.getIntValue(),
                                keeperState.getIntValue(),
                                path);
    }

    @Override
    public String toString() {
        return "WatchedEvent{" +
                "keeperState=" + keeperState +
                ", eventType=" + eventType +
                ", path='" + path + '\'' +
                '}';
    }
}
