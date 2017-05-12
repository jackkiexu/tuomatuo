package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.common.Time;
import org.slf4j.Logger;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class RateLogger {

    public RateLogger(Logger LOG) {
        this.LOG = LOG;
    }

    private final Logger LOG;
    private String msg = null;
    private long timestamp;
    private int count = 0;

    public void flush(){
        if(msg != null){
            if(count > 1){
                LOG.warn("[" + count + " times " + msg);
            }else if(count == 1){
                LOG.warn(msg);
            }
        }

        msg = null;
        count = 0;
    }


    public void rateLimitLog(String newMsg){
        long now = Time.currentElapsedTime();
        if(newMsg.equals(msg)){
            ++count;
            if(now - timestamp >= 100){
                flush();
                msg = newMsg;
                timestamp = now;
            }
        } else{
            flush();
            msg = newMsg;
            timestamp = now;
            LOG.warn(msg);
        }
    }
}
