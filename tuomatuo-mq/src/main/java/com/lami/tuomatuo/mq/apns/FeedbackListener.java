package com.lami.tuomatuo.mq.apns;

/**
 * Created by xjk on 2016/9/23.
 */
public interface FeedbackListener {

    void feedback(byte[] token, long timestamp);

}
