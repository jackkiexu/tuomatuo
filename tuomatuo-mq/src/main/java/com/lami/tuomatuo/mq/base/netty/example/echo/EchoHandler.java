package com.lami.tuomatuo.mq.base.netty.example.echo;

import com.lami.tuomatuo.mq.base.netty.buffer.ChannelBuffer;
import com.lami.tuomatuo.mq.base.netty.buffer.ChannelBuffers;
import com.lami.tuomatuo.mq.base.netty.channel.*;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xujiankang on 2016/9/29.
 */
@ChannelPipelineCoverage("all")
public class EchoHandler extends SimpleChannelHandler {

    private static final Logger logger = Logger.getLogger(EchoHandler.class);

    private ChannelBuffer firstMessage;
    private AtomicLong transferredNytes = new AtomicLong();

    public EchoHandler() {
        this(0);
    }

    public EchoHandler(int firstMessageSize) {
        if(firstMessageSize < 0){
            throw new IllegalArgumentException("firstMessageSize : " + firstMessageSize);
        }
        this.firstMessage = ChannelBuffers.buffer(firstMessageSize);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }
    }

    public Long getTransferredNytes() {
        return transferredNytes.get();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        e.getChannel().write(firstMessage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        transferredNytes.addAndGet(((ChannelBuffer)e.getMessage()).readableBytes());
        e.getChannel().write(e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.info("e:"+ e.getCause());
        e.getChannel().close();
    }
}
