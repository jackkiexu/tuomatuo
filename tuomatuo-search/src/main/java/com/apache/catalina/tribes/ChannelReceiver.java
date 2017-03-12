package com.apache.catalina.tribes;

import java.io.IOException;

/**
 * ChannelReceiver Interface
 * The <code>ChannelReceiver</code> interface is the data receiver component
 * at the bottom layer, the IO layer (for layers see the javadoc for the {@link Channel} interface)
 * This class may optionally implement a thread pool for parallel processing of incomng messages
 *
 * Created by xjk on 3/12/17.
 */
public interface ChannelReceiver extends Heartbeat{

    static final int MAX_UDP_SIZE = 65535;

    /**
     * Start listening for incoming message on the host/port
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * Stop listening for messages
     */
    void stop();

    /**
     * String representation of the IPv4 or IPv6 address that this host is listening
     * to
     * @return the host that this receiver is listening to
     */
    String getHost();

    /**
     * Returns the listening port
     * @return
     */
    int getPort();

    /**
     * Returns the secure listening port
     * @return port, -1 if a secure port is not activated
     */
    int getSecurePort();

    /**
     * Return the UDP port
     * @return port, -1 if the UDP port is not activated
     */
    int getUdpPort();

    /**
     * Sets the message listener to receive notification of incoming
     *
     * @param listener MessageListener
     */
    void setMessageListener(MessageListener listener);

    /**
     * Returns the message listener that is associated with this receiver
     * @return
     */
    MessageListener getMessageListener();
}
