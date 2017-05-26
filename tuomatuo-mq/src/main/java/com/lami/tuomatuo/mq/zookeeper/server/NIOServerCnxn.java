package com.lami.tuomatuo.mq.zookeeper.server;

import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.ServerStats;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class NIOServerCnxn extends ServerCnxn {

    private static final Logger LOG = LoggerFactory.getLogger(NIOServerCnxn.class);

    public Channel channel;
    public ChannelBuffer channelBuffer;
    public volatile boolean throttled;
    public ByteBuffer bb;
    public ByteBuffer bbLen = ByteBuffer.allocate(4);
    public long sessionId;
    public int sessionTimeout;
    public AtomicLong outstandingCount = new AtomicLong();

    /**
     * The ZooKeeperServer for this connection. May be null if the server
     * is not currently serving requests(for example if the server is not
     * an active quorum participant)
     */
    public volatile ZooKeeperServer zkServer;

    public NettyServerCnxnFactory factory;
    public boolean initialized;

    public NIOServerCnxn(Channel channel, ZooKeeperServer zkServer, NettyServerCnxnFactory factory) {
        this.channel = channel;
        this.zkServer = zkServer;
        this.factory = factory;
        if(this.factory.login != null){
            this.zooKeeperSaslServer = new ZooKeeperSaslServer(factory.login);
        }
    }


    @Override
    void close() {
        if(LOG.isDebugEnabled()){
            LOG.debug("close called for sessionId:0x", Long.toHexString(sessionId));
        }

        synchronized (factory.cnxns){
            // if this is not in cnxns then it's already closed
            if(!factory.cnxns.remove(this)){
                if(LOG.isDebugEnabled()){
                    LOG.debug("cnxns size:" + factory.cnxns.size());
                }
                return;
            }

            if(LOG.isDebugEnabled()){
                LOG.debug("close in progress for sessionId:0x" + Long.toHexString(sessionId));
            }
        }

        if(channel.isOpen()){
            channel.close();
        }

        factory.unregisterConnection(this);
    }


    @Override
    public long getSessionId() {
        return sessionId;
    }


    @Override
    int getSessionTimeout() {
        return sessionTimeout;
    }

    @Override
    public void process(com.lami.tuomatuo.mq.zookeeper.WatchedEvent event) {
        ReplyHeader h = new ReplyHeader(-1, -1L, 0);
        if(LOG.isTraceEnabled()){
            ZooTrace.logTraceMessage(LOG, ZooTrace.EVENT_DELIVERY_TRACE_MASK,
                    "Deliver event " + event + " to 0X"
                    + Long.toHexString(this.sessionId)
                    + " through " + this);
        }

        // Convert WatchedEvent to a type that can be sent over the wire
        WatcherEvent e = event.getWrapper();

        try{
            sendResponse(h, e, "notification");
        }catch (IOException e1){
            LOG.info("Problem sending to " + getRemoteSocketAddress(), e1 );
            close();
        }
    }

    private static final byte[] fourBytes = new byte[4];

    static class ResumeMessageEvent implements MessageEvent {
        public Channel channel;

        public ResumeMessageEvent(Channel channel) {
            this.channel = channel;
        }

        @Override
        public Object getMessage() {
            return null;
        }

        @Override
        public SocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public Channel getChannel() {
            return channel;
        }

        @Override
        public ChannelFuture getFuture() {
            return null;
        }
    }

    @Override
    public void sendResponse(ReplyHeader h, Record r, String tag) throws IOException {
        if(!channel.isOpen()){
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Make space for length
        BinaryOutputArchive bos = BinaryOutputArchive.getArchive(baos);
        try{
            baos.write(fourBytes);
            bos.writeRecord(h, "header");
            if(r != null){
                bos.writeRecord(r, tag);
            }
            baos.close();
        }catch (IOException e){
            LOG.info("Error serializing response");
        }

        byte b[] = baos.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.putInt(b.length - 4).rewind();
        sendBuffer(bb);
        if(h.getXid() > 0){
            // zks cannot be null otherwise we would not have gotten here!
            if(!zkServer.shouldThrottle(outstandingCount.decrementAndGet())){
                enableRecv();
            }
        }
    }

    @Override
    void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    void enableRecv() {
        if(throttled){
            throttled = false;
            LOG.info("Sending unthrottle event " + this);
            channel.getPipeline().sendUpstream(new ResumeMessageEvent(channel));
        }
    }

    @Override
    void sendBuffer(ByteBuffer sendBuffer) {
        if(sendBuffer == ServerCnxnFactory.closeConn){
            close();
            return;
        }
        packetSent();
    }

    /**
     * clean up the socket related to a command and also make sure sure we flush the
     * data before we do that
     * @param pwriter
     */
    private void cleanupWriteSocket(PrintWriter pwriter){
        try{
            if(pwriter != null){
                pwriter.flush();
                pwriter.close();
            }
        }catch (Exception e){
            LOG.info("Error closing PrintWriter ", e);
        }finally {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class wraps the sendBuffer method of NIOServerCnxn. It is
     * responsible for chunking up the response to a client. Rather
     * than cnxn'ing up a response fully in memory. which  may be large
     * for some  commands, this class chunks up the result
     */
    private class SendBufferWriter extends Writer{

        private StringBuffer sb = new StringBuffer();

        /**
         * Check if we already to send
         * @param force
         */
        private void checkFlush(boolean force){

        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {

        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }
    }


    @Override
    void sendCloseSession() {

    }








    @Override
    void disableRecv() {

    }

    @Override
    void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    protected com.lami.tuomatuo.mq.zookeeper.server.ServerStats serverStats() {
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
