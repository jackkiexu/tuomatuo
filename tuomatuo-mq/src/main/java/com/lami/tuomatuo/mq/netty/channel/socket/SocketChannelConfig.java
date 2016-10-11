package com.lami.tuomatuo.mq.netty.channel.socket;

import com.lami.tuomatuo.mq.netty.channel.ChannelConfig;

/**
 * Created by xjk on 2016/9/27.
 */
public interface SocketChannelConfig extends ChannelConfig {

    boolean isTcpNoDelay();

    void setTcpNoDelay(boolean tcpNoDelay);

    int getSoLinger();

    void setSoLinger(int soLinger);

    int getSendBufferSize();

    void setSendBufferSize(int sendBufferSize);

    int getReceiveBufferSize(int receiveBufferSize);

    void setReceiveBufferSize(int receiveBufferSize);

    boolean isKeepAlive();

    void setKeepAlive(boolean keepAlive);

    int getTrafficClass();

    void settrafficClass(int trafficClass);

    boolean isReuseAddress();

    void setReuseAddress(boolean reuseAddress);

    void setPerformancePreferences(int connectionTime, int latancy, int bandwidth);

}
