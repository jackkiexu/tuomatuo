package com.apache.catalina.tribes;

/**
 * <p>Title: MessageListener</p>
 *
 * <p>Description: The listener to be registered with the ChannelReceiver, internal Tribes component</p>
 *
 * Created by xjk on 3/12/17.
 */
public interface MessageListener {

    void messageReceived(ChannelMessage msg);
    boolean accept(ChannelMessage msg);
}
