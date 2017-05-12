package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Created by xujiankang on 2017/3/19.
 */
public interface Election {

    public Vote lookForleader() throws InterruptedException;

    public void shutdown();
}
