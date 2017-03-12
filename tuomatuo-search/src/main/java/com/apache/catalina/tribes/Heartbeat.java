package com.apache.catalina.tribes;

/**
 * Can be implemented by the ChannelListener and Membership listeners to receive heartbeat
 * notifications from the Channel
 *
 * Created by xjk on 3/12/17.
 */
public interface Heartbeat {

    /**
     * Heartbeat invocation for resources cleanup etc
     */
    void heartbeat();
}
