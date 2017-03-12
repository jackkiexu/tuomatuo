package com.apache.catalina.tribes;

import com.apache.catalina.tribes.transport.io.XByteBuffer;

import java.io.Serializable;

/**
 * Message that is passed through the interceptor stack after the
 * data serialized in the Channel object and then passed down to the
 * interceptor and eventually down the ChannelSender component
 *
 * Created by xjk on 3/12/17.
 */
public interface ChannelMessage extends Serializable{

    /**
     * Get the address that this message originated from.
     * Almost always <code>Channel.getLocalMember(boolean)</code>
     * This would be set to a different address
     * if the message was being relayed from a host other than the one
     * that originally sent it
     *
     * @return the source or reply-to address of this message
     */
    Member getAddress();

    /**
     * Sets the source or reply-to address of this message
     * @param member
     */
    void setAddress(Member member);

    /**
     * Timestamp of when the message was created
     * @return long timestamp in milliseconds
     */
    long getTimestamp();


    /**
     * Each message must have a globally unique id
     * interceptors heavily depend on this id for message processing
     * @return byte
     */
    byte[] getUniqueId();

    /**
     * The byte buffer that contains the actual message payload
     * @param buf
     */
    void setMessage(XByteBuffer buf);

    /**
     * The byte buffer that contains the actual message payload
     * @return
     */
    XByteBuffer getMessage();

    /**
     * The message options is a 32 bit flag set
     * that triggers interceptors and message behaviour
     * @return
     */
    int getOptions();

    /**
     * sets the option bits for this message
     * @param options
     */
    void setOptions(int options);

    /**
     * Shallow close, what gets cloned depends on the implementatin
     * @return
     */
    Object clone();

    /**
     * Deep clone, all fields MUST getcloned
     * @return
     */
    Object deepclone();

}
