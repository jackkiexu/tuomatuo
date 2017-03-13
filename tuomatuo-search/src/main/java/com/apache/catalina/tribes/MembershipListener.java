package com.apache.catalina.tribes;

/**
 * The membershipListener interface is used as a callback to the
 * membership service. It has two methods that will notify the listener
 * when a member has joined the group and when a member has disappeared
 *
 * Created by xjk on 3/13/17.
 */
public interface MembershipListener {

    /**
     * A member was added to the group
     * @param member the member that was added
     */
    void memberAdded(Member member);

    /**
     * A member was removed from the group
     * If the member left voluntarily, the member.getCommand will contain the Member.SHUTDOWN_PAYLOAD data
     * @param member Member
     */
    void memberDisappeared(Member member);
}
