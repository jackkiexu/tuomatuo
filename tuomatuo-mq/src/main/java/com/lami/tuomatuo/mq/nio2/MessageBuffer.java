package com.lami.tuomatuo.mq.nio2;

/**
 * A shared buffer which can contain many messages inside. A message gets a section of the buffer to use. If the
 * message outgrows the section in size, the message request a larger section and message is copied to that
 * larger section. The smaller section is then freed again
 *
 * Created by xjk on 10/30/16.
 */
public class MessageBuffer {

    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL = 4 * KB;
    private static final int CAPACITY_MEDIUM = 128 * KB;
    private static final int CAPACITY_LARGE = 1024 * KB;

    // package scope (default) - so that can be accessed from unit tests
    public byte[] smallMessageBuffer = new byte[1024 * 4 * KB]; // 1024 * 4KB message 4MB.
    public byte[] mediumMessageBuffer = new byte[128 * 128 * KB]; // 128 * 128 K messages = 16MB.
    public byte[] largeMessageBuffer = new byte[16 * 1 * MB]; // 16 * 1 MB messages = 16MB.




}
