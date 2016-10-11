package com.lami.sms.pipe;

import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/1/22.
 */
public  class SmsBase {
    protected Logger logger = Logger.getLogger(getClass());

    private Integer channelId;

    protected static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 128, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(256), new ThreadPoolExecutor.DiscardPolicy());

    public Integer getChannelId() {
        return channelId;
    }
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

}
