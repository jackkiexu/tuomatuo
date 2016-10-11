package com.lami.tuomatuo.mq.apns.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lami.tuomatuo.mq.apns.PushNotification;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by xjk on 2016/9/26.
 */
public class PushNotificationHandler extends SimpleChannelHandler implements ChannelFutureListener {

    private Logger logger = Logger.getLogger(PushNotificationHandler.class);

    protected BlockingQueue<PushNotification> queue;
    protected ChannelBuffer buffer;
    protected ObjectMapper mapper;

    /**
     * Initialize a new instance that handles notifications from the supplied queue
     * @param queue
     * @param mapper
     */
    public PushNotificationHandler(BlockingQueue<PushNotification> queue, ObjectMapper mapper) {
        this.queue = queue;
        this.mapper = mapper;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        buffer = ChannelBuffers.dynamicBuffer(ctx.getChannel().getConfig().getBufferFactory());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        PushNotification pn = (PushNotification)e.getMessage();

        Channel channel = ctx.getChannel();
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
        encode(pn, buf);;

        ChannelFuture f = e.getFuture();
        f.addListener(this);
        Channels.write(ctx, f, buf);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer input = (ChannelBuffer)e.getMessage();
        if(!input.readable()) return;

        buffer.discardReadBytes();
        buffer.writeBytes(input);
        decode(ctx, buffer);
    }

    public void operationComplete(ChannelFuture future) throws Exception {
        if(future.isSuccess()){
            queue.poll();
        }
    }

    protected void decode(ChannelHandlerContext ctx, ChannelBuffer buffer) throws Exception{
        while(buffer.readableBytes() > 6){
            if(buffer.readByte()== 8){
                byte status = buffer.readByte();
                long id = buffer.readUnsignedInt();
                logger.info("Error response for notification id : " + id + ", status code");
            }
        }
    }

    private void encode(PushNotification n, ChannelBuffer buf){
        try {
            n.encode(mapper, buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
