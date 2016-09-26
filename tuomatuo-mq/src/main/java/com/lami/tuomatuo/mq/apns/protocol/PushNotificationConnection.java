package com.lami.tuomatuo.mq.apns.protocol;

import com.lami.tuomatuo.mq.apns.PushNotification;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.concurrent.BlockingQueue;

/**
 * Created by xujiankang on 2016/9/26.
 */
public class PushNotificationConnection extends SimpleChannelHandler {

    private Logger logger = Logger.getLogger(PushNotificationConnection.class);

    private BlockingQueue<PushNotification> queue;
    private Channel channel;
    private boolean closed;

    public PushNotificationConnection(BlockingQueue<PushNotification> queue) {
        this.queue = queue;
    }

    public synchronized void closed(){
        if(!closed && channel != null){
            ConnectionWatchdog watchdog = channel.getPipeline().get(ConnectionWatchdog.class);
            watchdog.setReconnect(false);
            closed = true;
            channel.close();
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = ctx.getChannel();
        for(PushNotification cmd : queue){
            channel.write(cmd);
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if(closed){
            queue.clear();
            queue = null;
            channel = null;
        }
    }

    public void send(PushNotification notification){
        try {
            queue.put(notification);
            if(channel != null){
                channel.write(notification);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
