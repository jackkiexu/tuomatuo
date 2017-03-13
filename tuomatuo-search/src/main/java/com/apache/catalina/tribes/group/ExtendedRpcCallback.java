package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.Member;

import java.io.Serializable;

/**
 * Extension to the {@link RpcCallback} interface. Allows a RPC messager to get a confirmation if the reply
 * was sent successfully to the original sender
 *
 * Created by xjk on 3/13/17.
 */
public interface ExtendedRpcCallback extends RpcCallback {

    /**
     * The replay failed
     * @param request - the original message that requested the reply
     * @param response - the reply message ot the original message
     * @param member - the sender requested that reply
     * @param reason - the reason the reply failed
     */
    void replyFailed(Serializable request, Serializable response, Member member, Exception reason);


    /**
     * The reply succeeded
     * @param request the original message that requested the reply
     * @param response the reply message to the original message
     * @param member the sender requested that reply
     */
    void replySucceeded(Serializable request, Serializable response, Member member);
}
