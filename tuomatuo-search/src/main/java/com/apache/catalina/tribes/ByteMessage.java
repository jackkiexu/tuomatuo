package com.apache.catalina.tribes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A byte message is not serialized and deserialized by the channel
 * instead it is sent as a byte array
 * By default
 *
 * Created by xjk on 3/13/17.
 */
public class ByteMessage implements Externalizable{

    /**
     * Storage for the message to sent
     */
    private byte[] message;

    /**
     * Creates an empty byte message
     * Constructor also for deserialization
     */
    public ByteMessage() {
    }

    /**
     * Creates a byte message with h
     * @param message
     */
    public ByteMessage(byte[] message) {
        this.message = message;
    }

    /**
     * Returns the message contents of this byte message
     * @return byte[] - message contents, can be null
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * Sets the message contents of this byte message
     * @param message
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(message != null ? message.length : 0);
        if(message != null) out.write(message, 0, message.length);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int length = in.readInt();
        message = new byte[length];
        in.readFully(message);
    }
}
