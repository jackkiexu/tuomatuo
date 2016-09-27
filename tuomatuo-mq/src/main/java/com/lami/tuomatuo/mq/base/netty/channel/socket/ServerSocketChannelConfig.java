package com.lami.tuomatuo.mq.base.netty.channel.socket;

import com.lami.tuomatuo.mq.base.netty.channel.ChannelConfig;

/**
 * Created by xujiankang on 2016/9/27.
 */
public interface ServerSocketChannelConfig extends ChannelConfig {

    int getBacklog();

    void setBacklog(int backlog);

    boolean isReuseAddress();

    void setReuseAddress(boolean reuseAddress);

    int getReceiveBufferSize();

    void setReceiveBufferSize(int receiveBufferSize);

    void setPerformancePreferences(int connectionTime, int latency, int bandwidth);

}
