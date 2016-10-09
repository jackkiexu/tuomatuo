package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/26.
 */
public class DefaultChannelStateEvent extends DefaultChannelEvent implements ChannelStateEvent {

    private ChannelState state;
    private Object value;

    public DefaultChannelStateEvent(Channel channel, ChannelFuture future, ChannelState state, Object value) {
        super(channel, future);
        this.state = state;
        this.value = value;
    }

    public DefaultChannelStateEvent(Channel channel, ChannelFuture future) {
        super(channel, future);
    }

    public ChannelState getState() {
        return state;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        String parentString = super.toString();
        StringBuilder buf = new StringBuilder(parentString.length() + 64);
        buf.append(parentString);
        buf.append(" - (state: ");
        switch (getState()) {
            case OPEN:
                if (Boolean.TRUE.equals(getValue())) {
                    buf.append("OPEN");
                } else {
                    buf.append("CLOSED");
                }
                break;
            case BOUND:
                if (getValue() != null) {
                    buf.append("BOUND");
                } else {
                    buf.append("UNBOUND");
                }
                break;
            case CONNECTED:
                if (getValue() != null) {
                    buf.append("CONNECTED");
                } else {
                    buf.append("DISCONNECTED");
                }
                break;
            case INTEREST_OPS:
                switch (((Integer) getValue()).intValue()) {
                    case Channel.OP_NONE:
                        buf.append("OP_NONE");
                        break;
                    case Channel.OP_READ:
                        buf.append("OP_READ");
                        break;
                    case Channel.OP_WRITE:
                        buf.append("OP_WRITE");
                        break;
                    case Channel.OP_READ_WRITE:
                        buf.append("OP_READ_WRITE");
                        break;
                    default:
                        buf.append("OP_");
                        buf.append(getValue());
                        buf.append(" (?)");
                }
        }
        buf.append(')');
        return buf.toString();
    }
}
