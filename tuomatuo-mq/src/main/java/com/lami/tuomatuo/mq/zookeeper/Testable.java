package com.lami.tuomatuo.mq.zookeeper;

/**
 * Abstraction that exposes versions method useful for testing ZooKeeper
 * Created by xujiankang on 2017/3/16.
 */
public interface Testable {

    /**
     * Cause the Zookeeper instance to behave as if the session expired
     */
    void injectSessionExpiration();
}
