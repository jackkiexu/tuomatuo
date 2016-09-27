package com.lami.tuomatuo.mq.base.netty.channel.socket;

import com.lami.tuomatuo.mq.base.netty.channel.ChannelException;
import com.lami.tuomatuo.mq.base.netty.channel.ChannelPipelineFactory;
import com.lami.tuomatuo.mq.base.netty.util.ConvertUtil;

import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Map;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class DefaultServerSocketChannelConfig implements ServerSocketChannelConfig {

    private ServerSocket socket;
    private volatile int backlog;
    private volatile ChannelPipelineFactory pipelineFactory;

    public DefaultServerSocketChannelConfig(ServerSocket socket) {
        this.socket = socket;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        if (backlog < 1) {
            throw new IllegalArgumentException("backlog: " + backlog);
        }
        this.backlog = backlog;
    }

    public boolean isReuseAddress() {
        try {
            return socket.getReuseAddress();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setReuseAddress(boolean reuseAddress) {
        try {
            socket.setReuseAddress(reuseAddress);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public int getReceiveBufferSize() {
        try {
            return socket.getReceiveBufferSize();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        try {
            socket.setReceiveBufferSize(receiveBufferSize);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public void setOptions(Map<String, Object> options) {
        for (Map.Entry<String, Object> e: options.entrySet()) {
            setOption(e.getKey(), e.getValue());
        }
    }

    protected boolean setOption(String key, Object value) {
        if (key.equals("receiveBufferSize")) {
            setReceiveBufferSize(ConvertUtil.toInt(value));
        } else if (key.equals("reuseAddress")) {
            setReuseAddress(ConvertUtil.toBoolean(value));
        } else if (key.equals("backlog")) {
            setBacklog(ConvertUtil.toInt(value));
        } else {
            return false;
        }
        return true;
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return null;
    }

    public void setPiplineFactory(ChannelPipelineFactory piplineFactory) {
        if (pipelineFactory == null) {
            throw new NullPointerException("pipelineFactory");
        }
        this.pipelineFactory = pipelineFactory;
    }

    public int getConnectTimeoutMillis() {
        return 0;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {

    }

    public int getWriteTimeoutMillis() {
        return 0;
    }

    public void setWriteTimeoutMillis(int writeTimeoutMillis) {

    }
}
