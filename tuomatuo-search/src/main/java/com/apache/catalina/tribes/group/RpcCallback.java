package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.Member;

import java.io.Serializable;

/**
 * The RpcCallback interface is an interface for the Tribes channel to request a
 * response object to a request that came in
 *
 * Created by xjk on 3/13/17.
 */
public interface RpcCallback {

    /**
     *
     * @param msg Serializable
     * @param sender
     * @return Serializable - null if no reply should be sent
     */
    Serializable replyRequest(Serializable msg, Member sender);


    /**
     * If the reply has already been sent to the requesting thread,
     * the rpc callback can handle any data that comes in after the fact
     * @param msg Serializable
     * @param sender Member
     */
    void leftOver(Serializable msg, Member sender);

}
