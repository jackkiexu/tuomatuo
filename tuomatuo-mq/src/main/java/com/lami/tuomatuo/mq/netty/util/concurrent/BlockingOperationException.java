package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.io.Serializable;

/**
 * An {@link IllegalStateException} which is raised when a user performed a blocking operation
 * when the user is in an event loop thread. If a blocking operation is performed in an event loop
 * thread, the blocking operation will most likely enter a dead lock state, hence throwing this
 * exception.
 *
 * Created by xjk on 12/18/16.
 */
public class BlockingOperationException extends IllegalStateException{


    private static final long serialVersionUID = -3568947361155594264L;

    public BlockingOperationException() {}

    public BlockingOperationException(String s) {
        super(s);
    }

    public BlockingOperationException(Throwable cause) {
        super(cause);
    }

    public BlockingOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
