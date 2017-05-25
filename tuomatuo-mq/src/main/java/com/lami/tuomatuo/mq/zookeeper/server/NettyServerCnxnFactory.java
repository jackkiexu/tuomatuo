package com.lami.tuomatuo.mq.zookeeper.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executors;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class NettyServerCnxnFactory extends ServerCnxnFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServerCnxnFactory.class);

    public ServerBootstrap bootstrap;
    public Channel parentChannel;
    public ChannelGroup allCahnnels = new DefaultChannelGroup("zkServerCnxns");
    public HashMap<InetAddress, Set<NettyServerCnxn>> ipMap = new HashMap<>();
    public InetSocketAddress localAddress;
    public int maxClientCnxns = 60;

    @ChannelHandler.Sharable
    class CnxnChannelHandler extends SimpleChannelHandler{

        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            LOG.info("Channel closed " + e);
            allCahnnels.remove(ctx.getChannel());
        }

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            LOG.info("Channel connected " + e);

            allCahnnels.add(ctx.getChannel());
            NettyServerCnxn cnxn = new NettyServerCnxn(ctx.getChannel(),
                    zkServer, NettyServerCnxnFactory.this);

            ctx.setAttachment(cnxn);
            addCnxn(cnxn);
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            LOG.info("Channel disconnected " + e);
            NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
            if(cnxn != null){
                LOG.info("Channel disconnect caused close " + e);
                cnxn.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            LOG.info("Exception caught " + e, e.getCause());
            NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
            if(cnxn != null){
                LOG.info("Closing " + cnxn);
                cnxn.close();
            }
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            LOG.info("message received called " + e.getMessage());
            try{
                LOG.info("New message " + e.toString() + " from " + ctx.getChannel());
                NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
                synchronized (cnxn){
                    processMessage(e, cnxn);
                }
            }catch (Exception ex){
                LOG.info("Unexpected exception in receive", ex);
                throw ex;
            }
        }

        @Override
        public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
            LOG.info("write complete " + e);
        }


        private void processMessage(MessageEvent e, NettyServerCnxn cnxn){
            LOG.info(Long.toHexString(cnxn.sessionId) + "queueBuffer: " + cnxn.queuedBuffer);

            if(e instanceof NettyServerCnxn.ResumeMessageEvent){
                LOG.info("Received ResumeMessageEvent");
                if(cnxn.queuedBuffer != null){
                    LOG.info("processing queue "
                        + Long.toHexString(cnxn.sessionId)
                            + " queuedBuffer 0x "
                            + ChannelBuffers.hexDump(cnxn.queuedBuffer)
                    );


                    cnxn.receiveMessage(cnxn.queuedBuffer);
                    if(!cnxn.queuedBuffer.readable()){
                        LOG.debug("Processed queue - no bytes remaining");
                        cnxn.queuedBuffer = null;
                    }else{
                        LOG.info("Processed queue - bytes remaining");
                    }
                } else {
                    LOG.info("queue empty");
                }

                cnxn.channel.setReadable(true);
            } else {
                ChannelBuffer buf = (ChannelBuffer)e.getMessage();
                LOG.info(Long.toHexString(cnxn.sessionId) +
                        " buf 0x"
                        + ChannelBuffers.hexDump(buf));

                if(cnxn.throttled){
                    LOG.info("Received message wile throttled");
                    // we are throttled so we need to queue
                    if(cnxn.queuedBuffer == null){
                        LOG.info("allocating queue");
                        cnxn.queuedBuffer = dynamicBuffer(buf.readableBytes());
                    }

                    cnxn.queuedBuffer.writeBytes(buf);
                    LOG.info(Long.toHexString(cnxn.sessionId) +
                            " buf 0x"
                            + ChannelBuffers.hexDump(buf));
                } else {
                    LOG.info("not throttled");
                    if(cnxn.queuedBuffer != null){
                        LOG.info(Long.toHexString(cnxn.sessionId) +
                                " buf 0x"
                                + ChannelBuffers.hexDump(buf));
                    }
                    cnxn.queuedBuffer.writeBytes(buf);
                    cnxn.receiveMessage(cnxn.queuedBufer);
                    if(!cnxn.queuedBuffer.readable()){
                        LOG.debug("Processed queue - no bytes remaining");
                        cnxn.queuedBuffer = null;
                    } else {
                        LOG.info("Processed queue - bytes remaining");
                    }

                }else{
                    cnxn.receiveMessage(bug);
                    if(buf.readable()){
                        cnxn.queuedBuffer = dynamicBuffer(buf);

                    }
                }
            }
        }

    }

    public CnxnChannelHandler channelHandler = new CnxnChannelHandler();

    public NettyServerCnxnFactory() {
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()
                )
        );
        // parent channel
        bootstrap.setOption("resuseAddress", true);
        // child channels 关闭将小数据包合并成大数据包的算法
        bootstrap.setOption("child.tcpNoDelay", true);
        // set socket linger to off, so that the socket close does not block
        bootstrap.setOption("child.soLinger", -1);

        bootstrap.getPipeline().addLast("servercnxnfactory", channelHandler);

    }

    @Override
    public void closeSession(long sessionId) {
        LOG.info("closeSession sessionId : 0X " + sessionId);
        NettyServerCnxn[] allCnxns = null;
        synchronized (cnxns){
            allCnxns = cnxns.toArray(new NettyServerCnxn[cnxns.size()]);
        }

        for(){

        }
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public Iterable<ServerCnxn> getConnections() {
        return null;
    }



    @Override
    public void configure(InetSocketAddress addr, int maxClientCnxns) throws IOException {

    }

    @Override
    public int getMaxClientCnxnsPerHost() {
        return 0;
    }

    @Override
    public void setMaxClientCnxnsPerHost(int max) {

    }

    @Override
    public void startup(ZooKeeperServer zkServer) throws IOException, InterruptedException {

    }

    @Override
    public void join() throws InterruptedException {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void start() {

    }

    @Override
    public void closeAll() {

    }

    @Override
    public InetSocketAddress getlocalAddress() {
        return null;
    }
}
