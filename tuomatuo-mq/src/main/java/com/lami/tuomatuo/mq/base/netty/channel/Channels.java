package com.lami.tuomatuo.mq.base.netty.channel;

import java.net.SocketAddress;
import java.util.Map;

/**
 * Created by xujiankang on 2016/9/27.
 */
public class Channels {

    public static ChannelPipeline pipeline(){
        return new DefaultChannelPipeline();
    }

    public static ChannelPipeline pipeline(ChannelPipeline pipeline){
        ChannelPipeline newPipeline = pipeline();
        for(Map.Entry<String, ChannelHandler> e : pipeline.toMap().entrySet()){
            newPipeline.addLast(e.getKey(), e.getValue());
        }
        return newPipeline;
    }

    public static ChannelPipelineFactory pipelineFactory(final ChannelPipeline pipeline){
        return new ChannelPipelineFactory(){
            public ChannelPipeline getPipeline() throws Exception {
                return pipeline(pipeline);
            }
        };
    }

    // future factory methods
    public static ChannelFuture future(Channel channel){
        return future(channel, false);
    }

    public static ChannelFuture future(Channel channel, boolean cancellable){
        return new DefaultChannelFuture(channel, cancellable);
    }
    public static ChannelFuture successedFuture(Channel channel){
        if(channel instanceof AbstractChannel){
            return ((AbstractChannel) channel).getSuccessedFuture();
        }else{
            return new SuccessedChannelFuture(channel);
        }
    }

    public static ChannelFuture failedFuture(Channel channel, Throwable cause){
        return new FailedChannelFuture(channel, cause);
    }





    // event factory methods

    public static MessageEvent messageEvent(Channel channel, ChannelFuture future, Object message){
        return messageEvent(channel, future, message, null);
    }

    public static MessageEvent messageEvent(Channel channel, ChannelFuture future, Object message, SocketAddress remoteAddress){
        return new DefaultMessageEvent(channel, future, message, remoteAddress);
    }


    //event emission methods

    public static void fireChannelOpen(Channel channel){
        if(channel.getParent() != null){
            fireChildChannelStateChanged(channel.getParent(), channel);
        }
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.OPEN, Boolean.TRUE));
    }

    public static void fireChannelOpen(ChannelHandlerContext ctx, Channel channel){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.OPEN, Boolean.FALSE));
    }

    public static void fireChannelBound(Channel channel, SocketAddress localAddress){
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.BOUND, localAddress));
    }

    public static void fireChannelBound(ChannelHandlerContext ctx, Channel channel, SocketAddress localAddress){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.BOUND, localAddress));
    }

    public static void fireChannelConnected(Channel channel, SocketAddress remoteAddress){
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.CONNECTED, remoteAddress));
    }

    public static void fireChannelConnected(ChannelHandlerContext ctx, Channel channel, SocketAddress remoteAddress){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.CONNECTED, remoteAddress));
    }

    public static void fireMessageReceived(Channel channel, Object message){
        fireMessageReceived(channel, message, null);
    }

    public static void fireMessageReceived(Channel channel, Object message, SocketAddress remoteAddress){
        channel.getPipline().sendUpstream(new DefaultMessageEvent(channel, successedFuture(channel), message, remoteAddress));
    }

    public static void fireMessageReceived(ChannelHandlerContext ctx, Channel channel, Object message){
        ctx.sendUpstream(new DefaultMessageEvent(channel, successedFuture(channel), message, null));
    }

    public static void fireMessageReceived(ChannelHandlerContext ctx, Channel channel, Object message, SocketAddress remoteAddress){
        ctx.sendUpstream(new DefaultMessageEvent(channel, successedFuture(channel), message, remoteAddress));
    }


    public static void fireChannelInterestChanged(Channel channel, int interestOps){
        validateInterestOps(interestOps);
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.INTEREST_OPS, Integer.valueOf(interestOps)));
    }

    public static void fireChannelInterestChanged(ChannelHandlerContext ctx, Channel channel, int interestOps){
        validateInterestOps(interestOps);
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.INTEREST_OPS, Integer.valueOf(interestOps)));
    }

    public static void fireChannelDisconnected(Channel channel){
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.CONNECTED, null));
    }

    public static void fireChannelDisconnected(ChannelHandlerContext ctx, Channel channel){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.CONNECTED, null));
    }

    public static void fireChannelUnbound(ChannelHandlerContext ctx, Channel channel){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.BOUND, null));
    }

    public static void fireChannelClosed(Channel channel){
        channel.getPipline().sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.OPEN, Boolean.FALSE));
        if(channel.getParent() != null){
            fireChildChannelStateChanged(channel.getParent(), channel);
        }
    }

    public static void fireChannelClosed(ChannelHandlerContext ctx, Channel channel){
        ctx.sendUpstream(new DefaultChannelStateEvent(channel, successedFuture(channel), ChannelState.OPEN, Boolean.FALSE));
    }

    public static void fireExceptionCaught(Channel channel, Throwable cause){
        channel.getPipline().sendUpstream(new DefaultExceptionEvent(channel, successedFuture(channel), cause));
    }

    public static void fireExceptionCaught(ChannelHandlerContext ctx, Channel channel, Throwable cause){
        ctx.sendUpstream(new DefaultExceptionEvent(channel, successedFuture(channel), cause));
    }




    private static void fireChildChannelStateChanged(Channel channel, Channel childChannel){
        channel.getPipline().sendUpstream(new DefaultChildChannelStateEvent(channel, successedFuture(channel), childChannel));
    }

    public static ChannelFuture bind(Channel channel, SocketAddress localAddress){
        ChannelFuture future = future(channel);
        channel.getPipline().sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.BOUND, localAddress));
        return future;
    }

    public static void bind(ChannelHandlerContext ctx, Channel channel, ChannelFuture future, SocketAddress localAddress){
        ctx.sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.BOUND, localAddress));
    }

    public static ChannelFuture connect(Channel channel, SocketAddress remoteAddress){
        ChannelFuture future = future(channel, true);
        channel.getPipline().sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.CONNECTED, remoteAddress));
        return future;
    }

    public static void connect(ChannelHandlerContext ctx, Channel channel, ChannelFuture future, SocketAddress remoteAddress){
        ctx.sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.CONNECTED, remoteAddress));
    }

    public static ChannelFuture write(Channel channel, Object message){
        return write(channel, message, null);
    }

    public static void write(ChannelHandlerContext ctx, Channel channel, ChannelFuture future, Object message){
        write(ctx, channel, future, message, null);
    }

    public static ChannelFuture write(Channel channel, Object message, SocketAddress remoteaddress){
        ChannelFuture future = future(channel);
        channel.getPipline().sendDownstream(new DefaultMessageEvent(channel, future, message, remoteaddress));
        return future;
    }

    public static void write(ChannelHandlerContext ctx, Channel channel, ChannelFuture future, Object message, SocketAddress remoteAddress){
        ctx.sendDownstream(new DefaultMessageEvent(channel, future, message, remoteAddress));
    }

    public static ChannelFuture  setInterestOps(Channel channel, int interestOps){
        validateInterestOps(interestOps);
        validateDownstreamInterastOps(channel, interestOps);

        ChannelFuture future = future(channel);
        channel.getPipline().sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.INTEREST_OPS, Integer.valueOf(interestOps)));
        return future;
    }

    public static void setInterestOps(ChannelHandlerContext ctx, Channel channel, ChannelFuture future, int interestOps){
        validateInterestOps(interestOps);
        validateDownstreamInterastOps(channel, interestOps);

        ctx.sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.INTEREST_OPS, Integer.valueOf(interestOps)));
    }

    public static ChannelFuture disconnect(Channel channel){
        ChannelFuture future = future(channel);
        channel.getPipline().sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.CONNECTED, null));
        return future;
    }

    public static void disconnect(ChannelHandlerContext ctx, Channel channel, ChannelFuture future){
        ctx.sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.CONNECTED, null));
    }

    public static ChannelFuture close(Channel channel){
        ChannelFuture future = future(channel);
        channel.getPipline().sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.OPEN, Boolean.FALSE));
        return future;
    }

    public static void close(ChannelHandlerContext ctx, Channel channel, ChannelFuture future){
        ctx.sendDownstream(new DefaultChannelStateEvent(channel, future, ChannelState.OPEN, Boolean.FALSE));
    }

    private static void validateInterestOps(int interestOps){
        switch (interestOps){
            case Channel.OP_NONE:
            case Channel.OP_READ:
            case Channel.OP_WRITE:
            case Channel.OP_READ_WRITE:
                break;
            default:
                throw new IllegalArgumentException("Invalid interestOps:" + interestOps);

        }
    }

    private static void validateDownstreamInterastOps(Channel channel, int interestOps){
        if(((channel.getInterestOps() ^ interestOps) & Channel.OP_WRITE) != 0){
            throw new IllegalArgumentException("OP_WRITE can't be modified by user");
        }
    }

    private Channels(){}
}
