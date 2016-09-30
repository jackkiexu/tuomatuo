package com.lami.tuomatuo.mq.jafka.utils;

/**
 * A mockable interface for time functions
 *
 * Created by xujiankang on 2016/9/30.
 */
public interface Time {

    long NsPerUs = 1000l;

    long UsPerMs = 1000l;

    long MsPerSec = 1000l;

    long NsPerMs = NsPerUs * UsPerMs;

    long NsPerSec = NsPerMs * MsPerSec;

    long UsPerSec  = UsPerMs * MsPerSec;

    
}
