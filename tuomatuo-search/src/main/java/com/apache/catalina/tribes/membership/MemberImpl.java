package com.apache.catalina.tribes.membership;

import com.apache.catalina.tribes.Member;
import com.apache.catalina.tribes.transport.SenderState;
import com.apache.tomcat.util.res.StringManager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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


    @Override
    public String getName() {
        return null;
    }

    @Override
    public byte[] getHost() {
        return new byte[0];
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getSecurePort() {
        return 0;
    }

    @Override
    public int getUdpPort() {
        return 0;
    }

    @Override
    public long getMemberAliveTime() {
        return 0;
    }

    @Override
    public void setMemberAliveTime(long memberAliveTime) {

    }

    @Override
    public boolean isReady() {
        return SenderState.getSenderState(this).isReady();
    }

    @Override
    public boolean isSuspect() {
        return SenderState.getSenderState(this).isSuspect();
    }

    @Override
    public boolean isFailing() {
        return SenderState.getSenderState(this).isFailing();
    }

    @Override
    public byte[] getUniqueId() {
        return new byte[0];
    }

    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public void setPayload(byte[] payload) {

    }

    @Override
    public byte[] getCommand() {
        return new byte[0];
    }

    @Override
    public void setCommand(byte[] command) {

    }

    @Override
    public byte[] getDomain() {
        return new byte[0];
    }

    /**
     * Increment the message count
     */
    protected void inc() {
        msgCount.incrementAndGet();
    }

    /**
     * Create a data package to sen over the wire representing this member
     * This is faster than serialization
     * @return - the bytes for this member deserialized
     */
    public byte[] getData() {
        return getData(true);
    }

    @Override
    public byte[] getData(boolean getalive) {
        return getData(getalive, false);
    }

    @Override
    public synchronized int getDataLength() {
        return TRIBES_MBR_BEGIN.length + // start pkg
                4 + //data length
                8 + // alive time
                4 + // port
                4 + // secure port
                4 + // udp port
                1 + // host length
                host.length + // host
                4 + // domain length
                domain.length + // domain
                16 + // unique id
                4 + // payload length
                payload.length + // payload
                TRIBES_MBR_END.length // end pkg
                ;
    }

    @Override
    public synchronized byte[] getData(boolean getalive, boolean reset) {
        if(reset){
            dataPkg = null;
        }

        // Look in cache first
        if(dataPkg != null){
            if(getalive){
                // You's be surprised, but System.currentTimeMillis
                // shows up on the profiler
                long alive = System.currentTimeMillis() - getServiceStartTime();
            }
        }

        return new byte[0];
    }


    public long getServiceStartTime(){
        return serviceStartTime;
    }

    public void setHostname(String host) throws IOException{

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
