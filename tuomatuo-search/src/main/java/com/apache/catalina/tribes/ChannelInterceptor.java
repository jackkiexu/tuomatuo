package com.apache.catalina.tribes;

/**
 * A ChannelInterceptor is an interceptor that intercepts
 * messages and membership messages in the channel stack
 * This allows interceptors to midify the message or perform
 * other actions when a message is sent or received
 * Interceptor are tied together in a linked list
 *
 * Created by xjk on 3/13/17.
 */
public interface ChannelInterceptor extends MembershipListener, Heartbeat{


    /**
     * 
     * @param svc
     * @throws ChannelException
     */
    void stop(int svc) throws ChannelException;

    void fireInterceptorEvent(InterceptorEvent event);

    interface InterceptorEvent{
        int getEventType();
        String getEventTypesDesc();
        ChannelInterceptor getInterceptor();
    }

}
