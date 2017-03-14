package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.*;

/**
 * Abstract class for the interceptor base class
 * Created by xjk on 3/14/17.
 */
public abstract class ChannelInterceptorBase implements ChannelInterceptor{

    private ChannelInterceptor next;
    private ChannelInterceptor previous;
    private Channel channel;

    // default value, always process
    protected int optionFlag = 0;

    public ChannelInterceptorBase() {
    }

    public boolean okToProcess(int messageFlags){
        if(this.optionFlag == 0) return true;
        return ((optionFlag & messageFlags) == optionFlag);
    }

    public final void setNext(ChannelInterceptor next) {
        this.next = next;
    }

    public final ChannelInterceptor getNext() {
        return next;
    }

    public void setPrevious(ChannelInterceptor previous) {
        this.previous = previous;
    }

    public void setOptionFlag(int optionFlag) {
        this.optionFlag = optionFlag;
    }

    public ChannelInterceptor getPrevious() {
        return previous;
    }

    public int getOptionFlag() {
        return optionFlag;
    }

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if(getNext() != null) getNext().sendMessage(destination, msg, payload);
    }

    @Override
    public void messageReceived(ChannelMessage data) {
        if(getPrevious() != null){
            getPrevious().messageReceived(data);
        }
    }

    @Override
    public void memberAdded(Member member) {
        // notify upwards
        if(getPrevious() != null)getPrevious().memberAdded(member);
    }

    @Override
    public void memberDisappeared(Member member) {
        if(getPrevious() != null)getPrevious().memberDisappeared(member);
    }

    @Override
    public void heartbeat() {
        if(getNext() != null){
            getNext().heartbeat();
        }
    }

    @Override
    public boolean hasMembers() {
        if(getNext() != null){
            return getNext().hasMembers();
        }else{
            return false;
        }
    }

    /**
     * Get all current cluster members
     * @return
     */
    @Override
    public Member[] getMembers() {
        if(getNext() != null){
            return getNext().getMembers();
        }else {
            return null;
        }
    }

    @Override
    public Member getMember(Member mbr) {
        if(getNext() != null){
            return getNext().getMember(mbr);
        }else{
            return null;
        }
    }

    /**
     * Return the member that represents this node
     * @param incAliveTime boolean
     * @return
     */
    @Override
    public Member getLocalMember(boolean incAliveTime) {
        if(getNext() != null){
            return getNext().getLocalMember(incAliveTime);
        }else{
            return null;
        }
    }

    /**
     * Starts up the channel. This can be called multiple times for individual services to start
     * The svc parameter can be the logical or value of any constants
     * @param svc int value of <BR>
     * DEFAULT - will start all services <BR>
     * MBR_RX_SEQ - starts the membership receiver <BR>
     * MBR_TX_SEQ - starts the membership broadcaster <BR>
     * SND_TX_SEQ - starts the replication transmitter<BR>
     * SND_RX_SEQ - starts the replication receiver<BR>
     * @throws ChannelException if a startup error occurs or the service is already started.
     */
    @Override
    public void start(int svc) throws ChannelException {
        if(getNext() != null) getNext().start(svc);
    }

    /**
     * Shuts down the channel. This can be called multiple times for individual services to shutdown
     * The svc parameter can be the logical or value of any constants
     * @param svc int value of <BR>
     * DEFAULT - will shutdown all services <BR>
     * MBR_RX_SEQ - stops the membership receiver <BR>
     * MBR_TX_SEQ - stops the membership broadcaster <BR>
     * SND_TX_SEQ - stops the replication transmitter<BR>
     * SND_RX_SEQ - stops the replication receiver<BR>
     * @throws ChannelException if a startup error occurs or the service is already started.
     */
    @Override
    public void stop(int svc) throws ChannelException {
        if (getNext() != null) getNext().stop(svc);
        channel = null;
    }

    @Override
    public void fireInterceptorEvent(InterceptorEvent event) {
        //empty operation
    }

    /**
     * Return the channel that is related to this interceptor
     * @return Channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Set the channel that is related to this interceptor
     * @param channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
