package com.lami.tuomatuo.mq.jafka.network;

/**
 * Created by xjk on 9/25/16.
 */
public interface Transmission {

    void expectIncomplete();
    void expectComplete();
    boolean complete();

}
