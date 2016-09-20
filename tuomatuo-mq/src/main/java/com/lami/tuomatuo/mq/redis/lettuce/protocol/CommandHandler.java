package com.lami.tuomatuo.mq.redis.lettuce.protocol;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.util.concurrent.BlockingQueue;

/**
 * A netty {@link org.jboss.netty.channel.ChannelHandler} responsible for writing redis
 * Created by xjk on 9/16/16.
 */
public class CommandHandler extends SimpleChannelHandler {

    protected Logger logger = Logger.getLogger(CommandHandler.class);

    protected BlockingQueue<Command<?>> queue;
    protected ChannelBuffer buffer;
    protected RedisStateMachine rsm;

    /**
     * Initialize a new instance that handles commands from the supplied queue
     * @param queue
     */
    public CommandHandler(BlockingQueue<Command<?>> queue) {
        this.queue = queue;
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        buffer = ChannelBuffers.dynamicBuffer(ctx.getChannel().getConfig().getBufferFactory());
        rsm = new RedisStateMachine();
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Command cmd = (Command) e.getMessage();
        Channel channel = ctx.getChannel();
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
        cmd.encode(buf);
        Channels.write(ctx, e.getFuture(), buf);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ChannelBuffer input = (ChannelBuffer)e.getMessage();
        if(!input.readable()) return;

        buffer.discardReadBytes();;
        buffer.writeBytes(input);
        decode(ctx, buffer);
    }

    protected void decode(ChannelHandlerContext ctx, ChannelBuffer buffer) throws InterruptedException{
        while(!queue.isEmpty() && rsm.decode(buffer, queue.peek().getOutput())){
            Command<?> cmd = queue.take();
            cmd.complete();
        }
    }
}
