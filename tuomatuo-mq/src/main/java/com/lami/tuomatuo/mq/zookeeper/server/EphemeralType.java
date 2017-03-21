package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.CreateMode;

/**
 * Created by xujiankang on 2017/3/19.
 */
public enum EphemeralType {

    /** NOT ephemeral */
    VOID,

    /** Standard, pre-3.5x EPHEMERAL */
    NORMAL,

    /** Container node */
    CONTAINER,

    /** TTL node */
    TTL;

    public static final long CONTAINER_EPHEMERAL_OWNER = Long.MIN_VALUE;
    public static final long MAX_TTL = 0x0fffffffffffffffL;
    public static final long TTL_MASK = 0x8000000000000000L;


    public static EphemeralType get(long ephemeralOwner){
        if(ephemeralOwner == CONTAINER_EPHEMERAL_OWNER){
            return CONTAINER;
        }
        if(ephemeralOwner < 0){
            return TTL;
        }
        return (ephemeralOwner == 0) ? VOID : NORMAL;
    }

    public static void  validateTTL(CreateMode mode, long ttl){
        if(mode.isTTL()){
            ttlToEphemeralOwner(ttl);
        }else if(ttl >= 0){
            throw new IllegalArgumentException("ttl not valid for mode : " + mode);
        }
    }


    public static long getTTL(long ephemeralOwner){
        if((ephemeralOwner < 0) && (ephemeralOwner != CONTAINER_EPHEMERAL_OWNER)){
            return ephemeralOwner & MAX_TTL;
        }
        return 0;
    }

    public static long ttlToEphemeralOwner(long ttl){
        if((ttl > MAX_TTL) || (ttl <= 0)){
            throw new IllegalArgumentException("ttl must be positive and cannot be larger than :" + MAX_TTL);
        }
        return TTL_MASK | ttl;
    }
}
