package com.apache.tomcat.util.threads;

/**
 * A custom {@link RuntimeException} thrown by the {@link java.util.concurrent.ThreadPoolExecutor}
 * to signal that the thread should be disposed of
 *
 * Created by xjk on 3/9/17.
 */
public class StopPooledThreadException extends RuntimeException {

    private static final long serialVersionUID = 988238836264677517L;

    public StopPooledThreadException(String message) {
        super(message);
    }
}
