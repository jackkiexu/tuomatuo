package com.lami.tuomatuo.mq.jafka.api;

/**
 * Mark a calculable object(request/message/data...)
 * Created by xjk on 9/25/16.
 */
public interface ICalculable {

    /**
     * get the size of object(in bytes)
     * @return
     */
    int getSizeInBytes();
}
