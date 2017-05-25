package com.lami.tuomatuo.mq.zookeeper.client;

import java.net.InetSocketAddress;

/**
 * Created by xjk on 3/22/17.
 */
public interface HostProvider {


    public int size();

    /**
     * The next host to try to connect to.
     * For a spinDelay of 0 there should be no wait
     *
     * @param spinDelay Milliseconds to wait if all hosts have been tried once
     * @return
     */
    public InetSocketAddress next(long spinDelay);

    /**
     * Notify the HostProvider of a successful connection
     * The HostProvider may use this notification to reset it's inner state
     */
    public void onConnected();

}
