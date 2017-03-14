package com.apache.catalina.tribes.membership;

import com.apache.catalina.tribes.Member;
import com.apache.tomcat.util.res.StringManager;

import java.io.Externalizable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A <b>membership</b> implementation using simple multicast
 * This is the representation of a multicast member
 * Carries the host, and port of the this or other cluster nodes
 *
 * Created by xjk on 3/14/17.
 */
public class MemberImpl implements Member, Externalizable {

    /**
     * Should a call to getName or getHostName try to do a DNS lookup?
     * default is false
     */
    public static final boolean DO_DNS_LOOKUPS = Boolean.parseBoolean(System.getProperty("org.apache.catalina.tribes.dns_lookups","false"));

    public static final transient byte[] TRIBES_MBR_BEGIN = new byte[] {84, 82, 73, 66, 69, 83, 45, 66, 1, 0};
    public static final transient byte[] TRIBES_MBR_END   = new byte[] {84, 82, 73, 66, 69, 83, 45, 69, 1, 0};
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    /**
     * The listen host for this member
     */
    protected volatile byte[] host = new byte[0];
    protected transient volatile String hostname;

    /**
     * The tcp listen port for this member
     */
    protected volatile int port;

    /**
     * The udp listen port for this member
     */
    protected volatile int udpPort = -1;

    /**
     * The tcp/SSL listen port for this member
     */
    protected volatile int securePort = -1;

    /**
     * Counter for how many broadcast messages have been sent from this member
     */
    protected AtomicInteger msgCount = new AtomicInteger(0);

    /**
     * The number of millisecond since this member was
     * created. is kept track of using the start time
     */
    protected volatile long memberAliveTime = 0;

    /**
     * For the local member only
     */
    protected transient long serviceStartTime;

    /**
     * To avoid serialization over and over again, once the local dataPkg
     * has been set, we use that to transmit data
     */
    protected transient byte[] dataPkg = null;

    /**
     * Unique session Id for this member
     */
    protected volatile byte[] uniqueId = new byte[16];

    /**
     * Custom payload that an app framework can broadcast
     * Also used to transport stop command
     */
    protected volatile byte[] payload = new byte[0];

    /**
     * Command, so that the custom payload doesn't have to be used
     * This is for internal tribes use, such as SHUTDOWN_COMMAND
     */
    protected volatile byte[] command = new byte[0];

    /**
     * Domain if we want to filter based on domain
     */
    protected volatile byte[] domain = new byte[0];

    /**
     * The flag indicating that this member is a local member
     */
    protected volatile boolean local = false;


    /**
     * Empty constructor for serialization
     */
    public MemberImpl() {
    }

    /**
     * Construct a new member object
     *
     * @param host - the tcp listen host
     * @param port - the tcp listen port
     * @param aliveTime - the number of milliseconds since this member was created
     * @throws IOException If there is an error converting the host name to an
     *              IP address
     */
    public MemberImpl(String host, int port, long aliveTime) throws IOException {
        setHostname(host);
        this.port = port;
        this.memberAliveTime = aliveTime;
    }

    public MemberImpl(String host, int port, long aliveTime, byte[] payload) throws IOException {
        this(host, port, aliveTime);
        setPayload(payload);
    }



    public void setHostname(String host) throws IOException{

    }
}
