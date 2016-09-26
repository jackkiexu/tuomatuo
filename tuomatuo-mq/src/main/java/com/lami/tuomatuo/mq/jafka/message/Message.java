package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.api.ICalculable;
import com.lami.tuomatuo.mq.jafka.common.UnknownMagicByteException;

import java.nio.ByteBuffer;

/**
 * A message. The format of an N byte message is the following
 *
 * magic byte is 1
 *
 * <p>
 *     1. 1 byte "magic" identifier to allow format changes
 *     2. 1 byte "attributes" identifier to allow annotions on the message
 * independent of the version (e.g compression enabled, type of codec used)
 *     3. 4 byte CRC32 of the playload
 *     4. N-6 byte payload
 * </p>
 *
 * Created by xjk on 9/25/16.
 */
public class Message implements ICalculable {

    private static final byte MAGIC_VERSION2 = 1;

    public static final byte CurrentMagicValue = 1;

    public static final byte MagicOffset = 0;

    public static final byte MagicLength = 1;

    public static final byte AttributeOffset = MagicOffset + MagicLength;

    public static final byte AttributeLength = 1;

    /**
     * Specifies the mask for the compression code. 2 bits to hold the
     * compression codec. 0 is reserved to indicate no compression
     */
    public static final int CompressionCodeMask = 0x03;

    public static final int NoCompression = 0;

    /**
     * Computes the CRC value based on the magic byte
     * @param magic
     * @return
     */
    public static int crcOffset(byte magic){
        switch (magic){
            case MAGIC_VERSION2:
                return AttributeOffset + AttributeLength;
        }
        throw new UnknownMagicByteException(String.format("Magic byte value of %d is unknow", magic));
    }

    public static final byte CrcLength = 4;

    /**
     * Compute the offset to the message payload based on the magic byte
     *
     * @param magic Specifies the magic byte value. Possible values are 0
     *              and 1 0 for no compression 1 for compression
     * @return
     */
    public static int payloadOffset(byte magic) {
        return crcOffset(magic) + CrcLength;
    }

    /**
     * Compute the size of the message header based the magic byte
     *
     * @param magic Specifies the magic byte value. Possible values are 0
     *              and 1 0 for no compression 1 for compression
     * @return
     */
    public static int headerSize(byte magic){
        return payloadOffset(magic);
    }

    /** Size of the header for magic byte 0. This is the minimum size of any message header
     *
     */
    public static final int MinHeaderSize = headerSize((byte)1);

    ByteBuffer buffer;
    private int messageSize;

    public Message(ByteBuffer buffer) {
        this.buffer = buffer;
        this.messageSize = buffer.limit();
    }

    public Message(long checksum, byte[] bytes, CompressionCodec compressionCodec){
        this(ByteBuffer.allocate(Message.headerSize(Message.CurrentMagicValue) + bytes.length));
        buffer.put(CurrentMagicValue);
        byte attributes = 0;
        if(compressionCodec.codec > 0){
            attributes = (byte)(attributes | (CompressionCodeMask & compressionCodec.codec));
        }
        buffer.put(attributes);

    }

    public int getSizeInBytes() {
        return 0;
    }
}
