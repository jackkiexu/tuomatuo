package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class Vote {

    private static final Logger LOG = LoggerFactory.getLogger(Vote.class);

    public final int version;
    public final long id;
    public final long zxid;
    public final long electionEpoch;
    public final long peerEpoch;
    public final QuorumPeer.ServerState state;

    public Vote(long id, long zxid){
        this.version = 0x0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = -1;
        this.peerEpoch = -1;
        this.state = QuorumPeer.ServerState.LOOKING;
    }

    public Vote(long id, long zxid, long peerEpoch){
        this.version = 0x0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = -1;
        this.peerEpoch = peerEpoch;
        this.state = QuorumPeer.ServerState.LOOKING;
    }

    public Vote(long id,
                long zxid,
                long electionEpoch,
                long peerEpoch){
        this.version = 0x0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.peerEpoch = peerEpoch;
        this.state = QuorumPeer.ServerState.LOOKING;
    }

    public Vote(int version, long id,
                long zxid, long electionEpoch, long peerEpoch, QuorumPeer.ServerState state){
        this.version = version;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.state = state;
        this.peerEpoch = peerEpoch;
    }

    public Vote(long id, long zxid,
                long electionEpoch,
                long peerEpoch,
                QuorumPeer.ServerState state){
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.state = state;
        this.peerEpoch = peerEpoch;
        this.version = 0x0;
    }



    public long getPeerEpoch() {
        return peerEpoch;
    }

    public int getVersion() {
        return version;
    }

    public long getId() {
        return id;
    }

    public long getZxid() {
        return zxid;
    }

    public long getElectionEpoch() {
        return electionEpoch;
    }

    public QuorumPeer.ServerState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vote)) {
            return false;
        }
        Vote other = (Vote) o;


        /*
         * There are two things going on in the logic below.
         * First, we compare votes of servers out of election
         * using only id and peer epoch. Second, if one version
         * is 0x0 and the other isn't, then we only use the
         * leader id. This case is here to enable rolling upgrades.
         *
         * {@see https://issues.apache.org/jira/browse/ZOOKEEPER-1805}
         */
        if ((state == QuorumPeer.ServerState.LOOKING) ||
                (other.state == QuorumPeer.ServerState.LOOKING)) {
            return (id == other.id
                    && zxid == other.zxid
                    && electionEpoch == other.electionEpoch
                    && peerEpoch == other.peerEpoch);
        } else {
            if ((version > 0x0) ^ (other.version > 0x0)) {
                return id == other.id;
            } else {
                return (id == other.id
                        && peerEpoch == other.peerEpoch);
            }
        }
    }

    @Override
    public int hashCode() {
        return (int) (id & zxid);
    }

    public String toString() {
        return String.format("(%d, %s, %s)",
                id,
                Long.toHexString(zxid),
                Long.toHexString(peerEpoch));
    }
}
