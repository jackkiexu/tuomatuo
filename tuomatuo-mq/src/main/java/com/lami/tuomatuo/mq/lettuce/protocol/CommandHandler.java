package com.lami.tuomatuo.mq.lettuce.protocol;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.util.concurrent.BlockingQueue;

/**
 * A netty {@link org.jboss.netty.channel.ChannelHandler} responsible for encode/decode message for writing
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
        logger.info("CommandHandler writeRequested : " + cmd + " and content : " + new String(buf.copy().array()));
        Channels.write(ctx, e.getFuture(), buf);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("CommandHandler channelClosed");
        super.channelClosed(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        logger.info("CommandHandler messageReceived");
        ChannelBuffer input = (ChannelBuffer)e.getMessage();
        if(!input.readable()) return;

        buffer.discardReadBytes();;
        buffer.writeBytes(input);

        StringBuilder temp = new StringBuilder();
        for(byte b : buffer.array()){
            temp.append(b+",");
        }
        logger.info("StringBuilder temp :" + temp);

        logger.info("CommandHandler messageReceived : " + new String(buffer.array()));
        decode(ctx, buffer);
    }

    protected void decode(ChannelHandlerContext ctx, ChannelBuffer buffer) throws InterruptedException{
        try {
            if(buffer.array()[0] == 58){
                logger.info(new String(buffer.array()));
            }
            logger.info("queue: " + queue + ", queue() :" + queue.peek().getOutput().getClass());
            boolean rsmDecode = rsm.decode(buffer, queue.peek().getOutput());
            logger.info("queue.isEmpty() :" + queue.isEmpty() + ", rsmDecode:"+rsmDecode);

            while(!queue.isEmpty() && rsmDecode){
                Command<?> cmd = queue.take();
                cmd.complete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }
}
