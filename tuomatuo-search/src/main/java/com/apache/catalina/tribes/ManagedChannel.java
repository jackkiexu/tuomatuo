package com.apache.catalina.tribes;

import java.util.Iterator;

/**
 * Channel interface
 * A managed channel interface gives you access to the components of the channels
 * such as senders, receivers, interceptors etc for configurations purposes
 *
 * Created by xjk on 3/13/17.
 */
public interface ManagedChannel extends Channel {

    /**
     * Sets the channel sender
     * @param sender ChannelSender
     * @see ChannelSender
     */
    public void setChannelSender(ChannelSender sender);

    /**
     * Sets the channel receiver
     * @param receiver ChannelReceiver
     * @see ChannelReceiver
     */
    public void setChannelReceiver(ChannelReceiver receiver);

    /**
     * Sets the membership service
     * @param service MembershipService
     * @see MembershipService
     */
    public void setMembershipService(MembershipService service);

    /**
     * returns the channel sender
     * @return ChannelSender
     * @see ChannelSender
     */
    public ChannelSender getChannelSender();

    /**
     * returns the channel receiver
     * @return ChannelReceiver
     * @see ChannelReceiver
     */
    public ChannelReceiver getChannelReceiver();

    /**
     * Returns the membership service
     * @return MembershipService
     * @see MembershipService
     */
    public MembershipService getMembershipService();

    /**
     * Returns the interceptor stack
     * @return Iterator
     * @see Channel#addInterceptor(ChannelInterceptor)
     */
    public Iterator<ChannelInterceptor> getInterceptors();

}
