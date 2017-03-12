package com.apache.catalina.tribes;

import io.netty.channel.ChannelException;

import java.io.IOException;

/**
 * ChannelReceiver Interface
 * The <code>ChannelSender</code> interface is the data sender component
 * at the botten layer. the IO layer
 * The channel sender must support "silent" members, ie, be able to send a message to a member
 * that is not in the membership, but is part of the destination parameter
 *
 * Created by xjk on 3/12/17.
 */
public interface ChannelSender extends Heartbeat {

    /**
     * Notify the sender of a member being added to the group
     * Optional. This can be empty implementation, that does nothing
     *
     * @param member
     */
    void add(Member member);

    /**
     * Notification that a member has been removed or crashed
     * Can be used to clean up open connections ect
     * @param member Member
     */
    void remove(Member member);


    /**
     * Start the channel sender
     * @throws IOException if reprocessing takes place and an error happens
     */
    void start() throws IOException;

    /**
     * Stop the channel sender
     */
    void stop();

    /**
     * A channel heartbeat, use this method to clean up resources
     */
    @Override
    void heartbeat();

    /**
     * Send a message to one or more recipients
     * @param message ChannelMessage - the messageto be sent
     * @param destination Member[] - the destinations
     * @throws ChannelException
     */
    void sendMessage(ChannelMessage message, Member[] destination) throws ChannelException;
}
