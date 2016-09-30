package com.lami.tuomatuo.mq.jafka.api;

/**
 * Request Type
 *
 * Created by xujiankang on 2016/9/30.
 */
public enum RequestKeys {

    Produce, // 0
    Fetch, // 1
    MultiFetch, // 2
    MultiProduce, // 3
    Offset; // 4

    public int value = ordinal();

    static int size = values().length;

    public static RequestKeys valueOf(int ordinal){
        if(ordinal < 0 || ordinal >= size) return null;
        return values()[ordinal];
    }

}
