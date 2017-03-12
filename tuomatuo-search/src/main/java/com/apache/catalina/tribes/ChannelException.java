package com.apache.catalina.tribes;

/**
 * Channel Exception
 * A channel exception is thrown when an internal error happens
 * somewhere in the channel
 * When a global error happens, the cause can be retrieved using <code>getCause()</code>
 * If an application is sending a message and some of the recipients fail to receive it,
 * The application can retrieve what recipients failed by using the <code>getFaultyMembers()</code>
 * method. This way, an application will always know if a message was delivered successfully or not
 *
 * Created by xjk on 3/12/17.
 */
public class ChannelException extends Exception {

    private static final long serialVersionUID = -6078902885835008107L;


    /**
     * Title : FaultyMember class
     * Description: Represent a failure to a specific member when a message was sent
     * to more than one member
     */
    public static class FaultyMember {
        protected final Exception cause;
        protected final Member member;

        public FaultyMember(Exception cause, Member member) {
            this.cause = cause;
            this.member = member;
        }

        public Exception getCause() {
            return cause;
        }

        public Member getMember() {
            return member;
        }

        @Override
        public String toString() {
            return "FaultyMember{" +
                    "cause=" + cause +
                    ", member=" + member +
                    '}';
        }

        @Override
        public int hashCode() {
            return (member!=null)?member.hashCode():0;
        }

        @Override
        public boolean equals(Object o) {
            if (member==null || (!(o instanceof FaultyMember)) || (((FaultyMember)o).member==null)) return false;
            return member.equals(((FaultyMember)o).member);
        }
    }
}
