package com.lami.tuomatuo.mq.netty.channel;

import com.lami.tuomatuo.mq.netty.util.TimeBaseUuidGenerator;

import java.net.SocketAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xjk on 2016/9/26.
 */
public abstract class AbstractChannel implements Channel, Comparable<Channel> {

    private UUID id = TimeBaseUuidGenerator.generate();
    private Channel parent;
    private ChannelFactory factory;
    private ChannelPipeline pipeline;
    private ChannelFuture successedFuture = new SuccessedChannelFuture(this);

    private AtomicBoolean closed = new AtomicBoolean();
    private volatile int interestOps = OP_READ;

    /** Cache for the string representation of this channel */
    private String strVal;

    public AbstractChannel(
            Channel parent, ChannelFactory factory,
                           ChannelPipeline pipeline, ChannelSink sink) {
        this.parent = parent;
        this.factory = factory;
        this.pipeline = pipeline;
        pipeline.attach(this, sink);
    }

    public UUID getId(){
        return id;
    }

    public Channel getParent(){
        return parent;
    }

    public ChannelFactory getFactory(){
        return factory;
    }

    public ChannelPipeline getPipeline(){
        return pipeline;
    }

    protected ChannelFuture getSuccessedFuture(){
        return successedFuture;
    }

    protected ChannelFuture getSucceededFuture() {
        return successedFuture;
    }

    protected ChannelFuture getUnsupportedOperationFuture(){
        return new FailedChannelFuture(this, new UnsupportedOperationException());
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object o) {
        return this == o;
    }

    public int compareTo(Channel o){
        return System.identityHashCode(this) - System.identityHashCode(0);
    }

    public boolean isOpen(){
        return !closed.get();
    }

    public boolean setClosed(){
        return closed.compareAndSet(false, true);
    }

    public ChannelFuture bind(SocketAddress localAddress){
        return Channels.bind(this, localAddress);
    }

    public ChannelFuture close(){
        return Channels.close(this);
    }

    public ChannelFuture connect(SocketAddress remoteAddress){
        return Channels.connect(this, remoteAddress);
    }

    public ChannelFuture disconnect(){
        return Channels.disconnect(this);
    }

    public int getInterestOps(){
        return interestOps;
    }

    public ChannelFuture setInterestOps(int interestOps){
        return Channels.setInterestOps(this, interestOps);
    }

    protected void setInterestOpsNow(int interestOps){
        this.interestOps = interestOps;
    }

    public boolean isReadable(){
        return (getInterestOps() & OP_READ) != 0;
    }

    public boolean isWriteable(){
        return (getInterestOps() & OP_WRITE) == 0;
    }

    public ChannelFuture setReadable(boolean readable){
        if(readable){
            return setInterestOps(getInterestOps() | OP_READ);
        }else{
            return setInterestOps(getInterestOps() & ~OP_READ);
        }
    }

    public ChannelFuture write(Object message){
        return Channels.write(this, message);
    }

    public ChannelFuture write(Object message, SocketAddress remoteAddress){
        return Channels.write(this, message, remoteAddress);
    }


    public String toString() {
        if (strVal != null) {
            return strVal;
        }

        StringBuilder buf = new StringBuilder(128);
        buf.append(getClass().getSimpleName());
        buf.append("(id: ");
        buf.append(id.toString());

        if (isConnected()) {
            buf.append(", ");
            if (getParent() == null) {
                buf.append(getLocalAddress());
                buf.append(" => ");
                buf.append(getRemoteAddress());
            } else {
                buf.append(getRemoteAddress());
                buf.append(" => ");
                buf.append(getLocalAddress());
            }
        } else if (isBound()) {
            buf.append(", ");
            buf.append(getLocalAddress());
        }

        buf.append(')');

        return strVal = buf.toString();
    }



}
