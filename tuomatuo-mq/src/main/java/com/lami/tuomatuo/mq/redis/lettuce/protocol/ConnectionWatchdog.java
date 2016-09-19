package com.lami.tuomatuo.mq.redis.lettuce.protocol;

import com.lami.tuomatuo.mq.redis.lettuce.RedisConnection;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import java.net.SocketAddress;
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
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = ctx.getChannel();
        channels.add(channel);
        attempts = 0;
        ctx.sendUpstream(e);
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
        ctx.getChannel().close();
    }

    public void run(Timeout timeout) throws Exception {
        ChannelPipeline old = channel.getPipeline();
        CommandHandler handler = old.get(CommandHandler.class);
        RedisConnection connection = old.get(RedisConnection.class);
        ChannelPipeline pipeline = Channels.pipeline(this, handler, connection);

        Channel c = bootstrap.getFactory().newChannel(pipeline);
        c.getConfig().setOptions(bootstrap.getOptions());
        c.connect((SocketAddress)bootstrap.getOption("remoteAddress"));
    }

}
