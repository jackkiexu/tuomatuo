package com.apache.catalina.tribes.transport;

import java.io.IOException;

/**
 * Created by xujiankang on 2017/3/2.
 */
public interface DataSender {

    public void connect() throws IOException;
    public void disconnect();
    public boolean isConnected();
    public void setRxBufSize(int size);
    public void setTxBufSize(int size);
    public boolean keepalive();
    public void setTimeout(long timeout);
    public void setKeepAliveCount(int maxRequests);
    public void setKeepAliveTime(long keepAliveTimeInMs);
    public int getRequestCout();
    public long getConnectTime();

}
