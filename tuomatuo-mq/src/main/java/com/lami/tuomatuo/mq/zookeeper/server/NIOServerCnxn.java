package com.lami.tuomatuo.mq.zookeeper.server;

import org.apache.jute.Record;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.ServerStats;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class NIOServerCnxn extends ServerCnxn {
    @Override
    int getSessionTimeout() {
        return 0;
    }

    @Override
    void close() {

    }

    @Override
    public void sendResponse(ReplyHeader h, Record r, String tag) throws IOException {

    }

    @Override
    void sendCloseSession() {

    }

    @Override
    public void process(WatchedEvent event) {

    }

    @Override
    public long getSessionId() {
        return 0;
    }

    @Override
    void setSessionId(long sessionId) {

    }

    @Override
    void sendBuffer(ByteBuffer closeConn) {

    }

    @Override
    void enableRecv() {

    }

    @Override
    void disableRecv() {

    }

    @Override
    void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    protected ServerStats serverStats() {
        return null;
    }

    @Override
    public long getOutstandingRequests() {
        return 0;
    }

    @Override
    public long getAvagLatency() {
        return 0;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return null;
    }

    @Override
    public int getInterestOps() {
        return 0;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public Certificate[] getClientCertificateChain() {
        return new Certificate[0];
    }

    @Override
    public void setClientCertificateChain(Certificate[] chain) {

    }
}
