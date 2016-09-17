package com.lami.tuomatuo.mq.redis.lettuce.protocol;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 9/16/16.
 */
public class ConnectionWatchdog extends SimpleChannelHandler implements TimerTask {

    private ClientBootstrap bootstrap;
    private Channel channel;
    private ChannelGroup channels;
    private Timer timer;
    private boolean reconnect;
    private int attempts;

    public ConnectionWatchdog(ClientBootstrap bootstrap, ChannelGroup channels, Timer timer) {
        this.bootstrap = bootstrap;
        this.channels = channels;
        this.timer = timer;
    }

    public void setReconnect(boolean reconnect){
        this.reconnect = reconnect;
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if(reconnect){
            if(attempts < 8) attempts++;
            int timeout = 2 << attempts;
            timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
        }
        ctx.sendUpstream(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
        ctx.getChannel().close();
    }

    public void run(Timeout timeout) throws Exception {
        ChannelPipeline old = channel.getPipeline();
        CommandHandler handler = old.get(CommandHandler.class);


    }
}
