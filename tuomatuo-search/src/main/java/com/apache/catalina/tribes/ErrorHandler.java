package com.apache.catalina.tribes;


/**
 * The <code>ErrorHandler</code> class is used when sending messages
 * that are sent asynchronously and application still needs to get
 * confirmation when the message was sent successfully or when a message errored out
 * Created by xjk on 3/12/17.
 */
public interface ErrorHandler {

    /**
     * Invoked if the message is dispatched asynch, and an error occurs
     * @param x ChannelException - the error that happened
     * @param id the Unique id for the message
     */
    void handleError(ChannelException x, UniqueId id);

    /**
     * Invoked when the message has been set successfully
     * @param id the unique id for the message
     */
    void handleCompletion(UniqueId id);
}
