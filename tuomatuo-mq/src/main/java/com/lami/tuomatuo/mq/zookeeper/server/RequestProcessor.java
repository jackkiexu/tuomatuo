package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * RequestProcessors are chained together to process transactions. Requests are
 * always processed in order. The standalone server, follower, and leader all
 * have slightly different RequestProcessor chained together
 *
 * Requests always move forward through the chain of RequestProcessors. Requests
 * are passed to a RequestProcessor through processRequest() Generally method
 * will always be invoked by a single thread
 *
 * When shutdown is called, the requestProcessor should also shutdown
 * any RequestProcessors that is is connected to
 *
 * Created by xjk on 3/18/17.
 */
public interface RequestProcessor {

    public static class RequestProcessorException extends Exception{

        public RequestProcessorException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public RequestProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }


    void processRequest(Request request) throws RequestProcessorException;

    void shutdown();
}
