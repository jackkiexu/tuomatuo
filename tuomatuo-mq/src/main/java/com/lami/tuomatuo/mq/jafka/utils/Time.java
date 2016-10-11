package com.lami.tuomatuo.mq.jafka.utils;

/**
 * A mockable interface for time functions
 *
 * Created by xjk on 2016/9/30.
 */
public interface Time {

    long NsPerUs = 1000l;

    long UsPerMs = 1000l;

    long MsPerSec = 1000l;

    long NsPerMs = NsPerUs * UsPerMs;

    long NsPerSec = NsPerMs * MsPerSec;

    long UsPerSec  = UsPerMs * MsPerSec;

    long SecsPerMin = 60;

    long MinsPerHour = 60;

    long HoursPerDay = 24;

    long SecsPerHour = SecsPerMin * MinsPerHour;

    long SecsPerDay = SecsPerHour * HoursPerDay;

    long MinsPerDay = MinsPerHour * HoursPerDay;

    long milliseconds();

    long nanoseconds();

    void sleep(long ms) throws InterruptedException;

    public static final Time SystemTime = new Time(){

        public long milliseconds() {
            return System.currentTimeMillis();
        }

        public long nanoseconds(){
            return System.nanoTime();
        }

        public void sleep(long ms) throws InterruptedException{
            Thread.sleep(ms);
        }
    };


}
