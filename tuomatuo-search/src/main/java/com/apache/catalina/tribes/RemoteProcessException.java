package com.apache.catalina.tribes;

/**
 * Created by xjk on 3/12/17.
 */
/**
 * <p>Title: RemoteProcessException</p>
 *
 * <p>Description: Message thrown by a sender when USE_SYNC_ACK receives a FAIL_ACK_COMMAND.<br>
 * This means that the message was received on the remote node but the processing of the message failed.
 * This message will be embedded in a ChannelException.FaultyMember
 * </p>
 * @version 1.0
 */
public class RemoteProcessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RemoteProcessException() {
        super();
    }

    public RemoteProcessException(String message) {
        super(message);
    }

    public RemoteProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteProcessException(Throwable cause) {
        super(cause);
    }

}