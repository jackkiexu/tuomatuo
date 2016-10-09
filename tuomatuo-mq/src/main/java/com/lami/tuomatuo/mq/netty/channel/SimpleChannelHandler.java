package com.lami.tuomatuo.mq.netty.channel;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class SimpleChannelHandler implements ChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(SimpleChannelHandler.class);

    public void handlerUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        if(e instanceof MessageEvent){
            messageReceived(ctx, (MessageEvent) e);
        }else if(e instanceof ChildChannelStateEvent){
            ChildChannelStateEvent evt = (ChildChannelStateEvent)e;
            if(evt.getChildChannel().isOpen()){
                childChannelOpen(ctx, evt);
            }else{
                childChannelClosed(ctx, evt);
            }
        }else if(e instanceof ChannelStateEvent){
            ChannelStateEvent evt = (ChannelStateEvent) e;
            switch (evt.getState()){
                case OPEN:
                    if(Boolean.TRUE.equals(evt.getValue())){
                        channelOpen(ctx, evt);
                    }else{
                        channelClosed(ctx, evt);
                    }
                    break;
                case BOUND:
                    if(evt.getValue() != null){
                        channelBound(ctx, evt);
                    }else{
                        channelUnbound(ctx, evt);
                    }
                    break;
                case CONNECTED:
                    if(evt.getValue() != null){
                        channelConnected(ctx, evt);
                    }else{
                        channelDisconnected(ctx, evt);
                    }
                    break;
                case INTEREST_OPS:
                    channelInterestChanged(ctx, evt);
                    break;
            }
        }else if(e instanceof ExceptionEvent){
            exceptionCaught(ctx, (ExceptionEvent)e);
        }else{
            ctx.sendUpstream(e);
        }

    }


    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)throws Exception{
        if(this == ctx.getPipeline().getLast()){
            logger.info("Exception, please implement " + getClass().getName() + " .exceptionCaught() for proper handling.");
        }
        ctx.sendUpstream(e);
    }

    public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }

    public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception{
        ctx.sendUpstream(e);
    }
}
