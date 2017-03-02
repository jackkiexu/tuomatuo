package com.apache.catalina.tribes;

/**
 * The Member interface, defines a member in the group
 * Each member can carry a set of properties, defined by the actual implementation
 * A member is identified by the host/ip/uniqueId
 * The host is what interface the member is listening to, to receive data
 * The port is what port the memer is listening to, to receive data
 * The uniquedId defines the session id for the member. This is an important feature
 * since a member that has crashed and the starts up again on the same port/host is
 * not guaranteed to be the same member, so no state transfers will ever be confused
 *
 * Created by xujiankang on 2017/3/2.
 */
public interface Member {

    /**
     * When a member leaves the cluster, the payload of the memeberDisappeared member
     * will be the following bytes. This indicates a soft shutdown, and not a crash
     */
    public static final byte[] SHUTDOWN_PAYLOAD = new byte[]{66, 65, 66, 89, 45, 65, 76, 69, 88};

    /**
     * Returns the name of this node, should be unique within the group
     */
    public String getName();

    /**
     * Returns the listen for the ChannelReceiver implementation
     * @return IPv4 and IPv6 representation of the host address this member listens to incoming data
     * @see ChannelReceiver
     */
    public byte[] getHost();

    /**
     * Returns the listen port for the ChannelReceiver implementation
     * @return the listen port for the this member, -1 if its not listening on an insecure port
     * @see ChannelReceiver
     */
    public int getPort();

    /**
     * Returns the secure listen port for the ChannelReceiver implementation
     * Return -1 if its not listening to a secure port
     * @return the listen port for the member, -1 if its not listening on a secure port
     * @see ChannelReceiver
     */
    public int getSecurePort();

    /**
     * Return UDP port that this member is listening to for UDP message
     * @return the listen UDP port for this member, -1 if its not listening on a UDP port
     */
    public int getUdpPort();

    /**
     * Contains information on how long member has been online
     * The result is the number of milli seconds this member has been
     * broadcasting its memebership to the group
     * @return nr of millisecond since this member started
     */
    public long getMemberAliveTime();

    public void setMemberAliveTime(long memberAliveTime);

    /**
     * The current state of the memeber
     * @return boolean - true if the memeber is functioning correctly
     */
    public boolean isReady();

    /**
     * The current state of the member
     * @return boolean - true if the member is suspect, but the crash has not been confirmed
     */
    public boolean isSuspect();

    /**
     * @return boolean - true if the member has been confirmed to malfunction
     */
    public boolean isFailing();

    /**
     * returns UUID unique for this memeber over all sessions
     * If the member crashes and restarts, the uniqueId will be different
     * @return
     */
    public byte[] getUniqueId();

    /**
     * returns the payload associated with memeber
     * @return
     */
    public byte[] getPayload();

    public void setPayload(byte[] payload);

    /**
     * returns the command associated with this member
     * @return
     */
    public byte[] getCommand();

    public void setCommand();

    /**
     * Domain for this cluster
     * @return
     */
    public byte[] getDomain();

    /**
     * Highly optimized version of serializing a member into a byte array
     * Return a cached byte[] reference, do not modify this data
     * @param getalive calculate memberAlive time
     * @return
     */
    public byte[] getData(boolean getalive);

    /**
     * Highly optimized version of serializing a member into a byte array
     * Returns a cahced byte[] reference, do not modify this data
     * @param getalive calculate memberAlive time
     * @param reset reset the cached data package, and create a new one
     * @return
     */
    public byte[] getData(boolean getalive, boolean reset);

    /**
     * Length of a message obtained by {@link #"getData()}
     * @return
     */
    public int getDataLength();
}
