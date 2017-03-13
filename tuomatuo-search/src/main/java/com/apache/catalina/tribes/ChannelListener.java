package com.apache.catalina.tribes;

import java.io.Serializable;

/**
 *
 * Description: An interface to listens to incoming messages from a channel
 * When a message is received. the Channel will invoke the channel listener in a conditional sequence
 * <code>if(listener.accept(msg, sender)) listener.messageReceived(msg, sender)</code>
 * A ChannelListener implementation MUST NOT return true on <code>accept(Serializable, Member)</code>
 * If it doesn't intend to process the message, The channel can this way track whether a message
 * was processed by an above application or if it was just received and forgot about , a feature required
 * to support message-response(RPC) calls
 * Created by xjk on 3/13/17.
 */
public interface ChannelListener {


    /**
     * Receive a message from the channel
     * @param msg Serializable
     * @param sender - the source of the message
     */
    public void messageReceived(Serializable msg, Member sender);

    /**
     * Invoked by the channel to determine if the listener will process this message or not.
     * @param msg Serializable
     * @param sender Member
     * @return boolean
     */
    public boolean accept(Serializable msg, Member sender);

    /**
     *
     * @param listener Object
     * @return boolean
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object listener);

    /**
     *
     * @return int
     * @see Object#hashCode()
     */
    @Override
    public int hashCode();


}
