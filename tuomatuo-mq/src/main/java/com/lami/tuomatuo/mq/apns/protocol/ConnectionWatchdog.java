package com.lami.tuomatuo.mq.apns.protocol;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/** A netty {@link com.lami.tuomatuo.mq.netty.channel.ChannelHandler} responsible for monitor the channel and reconnecting when the connection is lost
 *
 * Created by xjk on 2016/9/26.
 */
public class ConnectionWatchdog extends SimpleChannelHandler implements TimerTask {

    private static final Logger logger = Logger.getLogger(ConnectionWatchdog.class);

    private ClientBootstrap bootstrap;
    private Channel channel;
    private ChannelGroup channels;
    private Timer timer;
    private boolean reconnect;
    private int attempts;

    /**
     * Create a new watchdog that adds to new connections to the supplied {@link ChannelGroup}
     * and established a new {@link Channel} when disconnected. while reconnect is true
     *
     * @param channels
     * @param bootstrap
     * @param timer
     */
    public ConnectionWatchdog(ChannelGroup channels, ClientBootstrap bootstrap, Timer timer) {
        this.channels = channels;
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.reconnect = true;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
       if(reconnect){
           if(attempts < 8) attempts++;
           int timeout = 2 << attempts;
           timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
           logger.info("Disconnected, reconnected in " + timeout);
       }
        ctx.sendUpstream(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("Exception caught" + e.getCause());
        ctx.getChannel().close();
    }

    /**
     * Reconnect to the remote address that the closed channel was connected to
     *
     * @param timeout
     * @throws Exception
     */
    public void run(Timeout timeout) throws Exception {
        bootstrap.connect(channel.getRemoteAddress());
    }
}
