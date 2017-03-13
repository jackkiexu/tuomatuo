package com.apache.catalina.tribes;

import java.util.Properties;

/**
 * MembershipService Interface
 * The <code>MembershipService</code> interface is the membership component
 * at the bottom layer, the IO layer (for layers see the javadoc for the {@link Channel} interface)
 *
 * Created by xjk on 3/13/17.
 */
public interface MembershipService {

    static final int MBR_RX = Channel.MBR_RX_SEQ;
    static final int MBR_TX = Channel.MBR_TX_SEQ;

    /**
     * Sets the properties for the membership service. This must be called before
     * the <code>start()</code> method is called
     * The properties are implementation specific
     * @param properties to be used to configure the membership service
     */
    void setProperties(Properties properties);

    /**
     * Return the properties for the configuration used
     * @return
     */
    Properties getProperties();


    /**
     * Starts the membership service. If a membership listeners is added
     * the listener will start to receive membership events
     * Performs a start level a and 2
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * Starts the membership service. If a membership listeners is added
     * the listener will start to receive membership events.
     * @param level - level MBR_RX starts listening for members, level MBR_TX
     * starts broad casting the server
     * @throws java.lang.Exception if the service fails to start.
     * @throws java.lang.IllegalArgumentException if the level is incorrect.
     */
    public void start(int level) throws java.lang.Exception;


    /**
     * Starts the membership service. If a membership listeners is added
     * the listener will start to receive membership events.
     * @param level - level MBR_RX stops listening for members, level MBR_TX
     * stops broad casting the server
     * @throws java.lang.IllegalArgumentException if the level is incorrect.
     */

    public void stop(int level);

    /**
     * @return true if the the group contains members
     */
    public boolean hasMembers();


    /**
     *
     * @param mbr Member
     * @return Member
     */
    public Member getMember(Member mbr);
    /**
     * Returns a list of all the members in the cluster.
     */

    public Member[] getMembers();

    /**
     * Returns the member object that defines this member
     */
    public Member getLocalMember(boolean incAliveTime);

    /**
     * Return all members by name
     */
    public String[] getMembersByName() ;

    /**
     * Return the member by name
     */
    public Member findMemberByName(String name) ;

    /**
     * Sets the local member properties for broadcasting
     */
    public void setLocalMemberProperties(String listenHost, int listenPort, int securePort, int udpPort);

    /**
     * Sets the membership listener, only one listener can be added.
     * If you call this method twice, the last listener will be used.
     * @param listener The listener
     */
    public void setMembershipListener(MembershipListener listener);

    /**
     * removes the membership listener.
     */
    public void removeMembershipListener();

    /**
     * Set a payload to be broadcasted with each membership
     * broadcast.
     * @param payload byte[]
     */
    public void setPayload(byte[] payload);

    public void setDomain(byte[] domain);

    /**
     * Broadcasts a message to all members
     * @param message
     * @throws ChannelException
     */
    public void broadcast(ChannelMessage message) throws ChannelException;
}
