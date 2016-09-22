package com.lami.tuomatuo.mq.base.netty.channel;

import java.util.Map;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelConfig {

    void setOptions(Map<String, Object> options);

    ChannelPiplineFactory getPipelineFactory();

    void setPiplineFactory(ChannelPiplineFactory piplineFactory);

    int getConnectTimeoutMillis();

    void setConnectTimeoutMillis(int connectTimeoutMillis);

    int getWriteTimeoutMillis();

    void setWriteTimeoutMillis(int writeTimeoutMillis);

}