package com.lami.tuomatuo.mq.netty.channel.socket;

import com.lami.tuomatuo.mq.netty.channel.ChannelException;
import com.lami.tuomatuo.mq.netty.channel.ChannelPipelineFactory;
import com.lami.tuomatuo.mq.netty.util.ConvertUtil;

import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

/**
 * Created by xjk on 2016/9/27.
 */
public class DefaultSocketChannelConfig implements SocketChannelConfig {

    private Socket socket;
    private volatile int connectTimeoutMillis = 10000; // 10 seconds

    public DefaultSocketChannelConfig(Socket socket) {
        if(socket == null){
            throw new NullPointerException("socket");
        }
        this.socket = socket;
    }



    public boolean isTcpNoDelay() {
        try {
            return socket.getTcpNoDelay();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        try {
            socket.setTcpNoDelay(tcpNoDelay);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public int getSoLinger() {
        try {
            return socket.getSoLinger();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setSoLinger(int soLinger) {
        try {
            if (soLinger < 0) {
                socket.setSoLinger(false, 0);
            } else {
                socket.setSoLinger(true, soLinger);
            }
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public int getSendBufferSize() {
        try {
            return socket.getSendBufferSize();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setSendBufferSize(int sendBufferSize) {
        try {
            socket.setSendBufferSize(sendBufferSize);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public int getReceiveBufferSize(int receiveBufferSize) {
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

    public boolean isKeepAlive() {
        try {
            return socket.getKeepAlive();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void setKeepAlive(boolean keepAlive) {
        try {
            socket.setKeepAlive(keepAlive);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public int getTrafficClass() {
        try {
            return socket.getTrafficClass();
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
    }

    public void settrafficClass(int trafficClass) {
        try {
            socket.setTrafficClass(trafficClass);
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
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

    public void setPerformancePreferences(int connectionTime, int latancy, int bandwidth) {
        socket.setPerformancePreferences(connectionTime, latancy, bandwidth);
    }

    public void setOptions(Map<String, Object> options) {
        for(Map.Entry<String, Object> e : options.entrySet()){
            setOption(e.getKey(), e.getValue());
        }
    }

    protected boolean setOption(String key, Object value){
        if (key.equals("receiveBufferSize")) {
            setReceiveBufferSize(ConvertUtil.toInt(value));
        } else if (key.equals("sendBufferSize")) {
            setSendBufferSize(ConvertUtil.toInt(value));
        } else if (key.equals("tcpNoDelay")) {
            setTcpNoDelay(ConvertUtil.toBoolean(value));
        } else if (key.equals("keepAlive")) {
            setKeepAlive(ConvertUtil.toBoolean(value));
        } else if (key.equals("reuseAddress")) {
            setReuseAddress(ConvertUtil.toBoolean(value));
        } else if (key.equals("soLinger")) {
            setSoLinger(ConvertUtil.toInt(value));
        } else if (key.equals("trafficClass")) {
            settrafficClass(ConvertUtil.toInt(value));
        } else if (key.equals("writeTimeoutMillis")) {
            setWriteTimeoutMillis(ConvertUtil.toInt(value));
        } else if (key.equals("connectTimeoutMillis")) {
            setConnectTimeoutMillis(ConvertUtil.toInt(value));
        } else if (key.equals("pipelineFactory")) {
            setPiplineFactory((ChannelPipelineFactory) value);
        } else {
            return false;
        }
        return true;
    }




    public ChannelPipelineFactory getPipelineFactory() {
        return null;
    }

    public void setPiplineFactory(ChannelPipelineFactory piplineFactory) {

    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException("connectTimeoutMillis: " + connectTimeoutMillis);
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getWriteTimeoutMillis() {
        return 0;
    }

    public void setWriteTimeoutMillis(int writeTimeoutMillis) {

    }
}
