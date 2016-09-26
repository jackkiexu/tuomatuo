package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/26.
 */
public class DefaultChildChannelStateEvent extends DefaultChannelEvent implements ChildChannelStateEvent {

    private Channel childChannel;

    public DefaultChildChannelStateEvent(Channel channel, ChannelFuture future, Channel childChannel) {
        super(channel, future);
        this.childChannel = childChannel;
    }

    public DefaultChildChannelStateEvent(Channel channel, ChannelFuture future) {
        super(channel, future);
    }

    public Channel getChildChannel() {
        return childChannel;
    }

    @Override
    public String toString() {
        String parentString = super.toString();
        StringBuilder buf = new StringBuilder(parentString.length() + 32);
        buf.append(parentString);
        buf.append(" - (childId: ");
        buf.append(getChildChannel().getId().toString());
        buf.append(", childState: ");
        buf.append(getChildChannel().isOpen()? "OPEN" : "CLOSE");
        buf.append(')');
        return buf.toString();
    }
}
