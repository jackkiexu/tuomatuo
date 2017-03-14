package com.apache.catalina.tribes.membership;

import com.apache.catalina.tribes.Channel;
import com.apache.catalina.tribes.MembershipListener;
import com.apache.catalina.tribes.MessageListener;
import com.apache.catalina.tribes.util.ExecutorFactory;
import com.apache.juli.logging.Log;
import com.apache.juli.logging.LogFactory;
import com.apache.tomcat.util.res.StringManager;
import org.aspectj.weaver.MemberImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A membership implementation using simple multicast
 * This is the representation of a multicast membership service
 * This class is responsible for maintaining a list of active cluster nodes in the cluster
 * If a node fails to send out a heartbeat, the node will be dismissed
 * This is the low level implementation that handles the multicast sockets
 * Need to fix this, could use java.nio and only need one thread to send and receive, or
 * just use a timeout on the receive
 *
 * Created by xjk on 3/14/17.
 */
public class McastServiceImpl {

    private static final Log log = LogFactory.getLog( McastService.class );

    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    protected static final int MAX_PACKET_SIZE = 65535;

    /**
     * Internal flag used for the listen thread that listens to the multicasting socket
     */
    protected volatile boolean doRunSender = false;
    protected volatile boolean doRunReceiver = false;
    protected int startLevel = 0;

    /**
     * Socket that we intend to listen to
     */
    protected MulticastSocket socket;

    /**
     * The local member that we intend to broad cast over and over again
     */
    protected final MemberImpl member;

    /**
     * The multicast address
     */
    protected final InetAddress address;

    /**
     * The multicast port
     */
    protected final int port;

    /**
     * The time it takes for a member to expire
     */
    protected final long timetoExpiration;

    /**
     * How often to we send out a brocast saying we are alive. must be smaller than timeToExpiration
     */
    protected final long sendFrequency;

    /**
     * Reuse the sendPacket, no need to create a new one everytime
     */
    protected DatagramPacket sendPacket;

    /**
     * Reuse the receivePacket, no need to create a new one everytime
     */
    protected DatagramPacket receivePacket;

    /**
     * The membership, used so that we calculate memberships when they arrive or don't arrive
     */
    protected Membership membership;

    /**
     * The actual listener for broadcast callbacks
     */
    protected final MembershipListener service;

    /**
     * The actual listener for broadcast callbacks
     */
    protected final MessageListener msgservice;

    /**
     * Thread to listen for pings
     */
    protected ReceiverThread receiver;

    /**
     * Thread to send pings
     */
    protected SenderThread sender;


    /**
     * Time to live for the multicast packets that are being sent out
     */
    protected final int mcastTTL;

    /**
     * Read timeout on the mcast socket
     */
    protected int mcastSoTimeout = -1;

    /**
     * bind address
     */
    protected final InetAddress mcastBindAddress;

    /**
     * nr of times the system has to fail before a recovery is initiated
     */
    protected int recoveryCounter = 10;

    /**
     * The time the recovery thread sleeps between recovery attempts
     */
    protected long recoverySleepTime = 5000;

    /**
     * Add the ability to turn off/on recovery
     */
    protected boolean recoveryEnabled = true;

    /**
     * Dont interrupt the sender / receiver thread, but pass off to an executor
     */
    protected final ExecutorService executor = ExecutorFactory.newThreadPool(0, 2, 2, TimeUnit.SECONDS);

    /**
     * disable/enable local loopback message
     */
    protected final boolean localLoopbackDisabled;

    private Channel channel;


    public McastServiceImpl(
            MemberImpl member,
            long sendFrequency,
            long expireTime,
            int port,
            InetAddress bind,
            InetAddress mcastAddress,
            int ttl,
            int soTimeout,
            MembershipListener service,
            MessageListener msgservice,
            boolean localLoopbackDisabled
    )throws IOException
    {
        this.member = member;
        this.address = mcastAddress;
        this.port = port;
        this.mcastSoTimeout = soTimeout;
        this.mcastTTL = ttl;
        this.mcastBindAddress = bind;
        this.timetoExpiration = expireTime;
        this.service = service;
        this.msgservice = msgservice;
        this.sendFrequency = sendFrequency;
        this.localLoopbackDisabled = localLoopbackDisabled;
        init();
    }

    public void init() throws IOException{
        setupSocket();
        sendPacket = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
        sendPacket.setAddress(address);
        sendPacket.setPort(port);

        receivePacket = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
        receivePacket.setAddress(address);
        receivePacket.setPort(port);

        member.setCommand(new byte[0]);
        if(membership == null){
            membership = new Membership(member);
        }

    }

    protected void setupSocket() throws IOException{

    }

}
