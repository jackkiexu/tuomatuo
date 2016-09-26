package com.lami.tuomatuo.mq.jafka.network;

import org.apache.log4j.Logger;

/**
 * Created by xjk on 9/25/16.
 */
public class AbstractTransmission implements Transmission {

    protected static final Logger logger = Logger.getLogger(AbstractTransmission.class);

    protected boolean over = false;


    public void expectIncomplete() {
        if(complete()){
            throw new IllegalStateException("This operation cannot be completed on a complete request");
        }
    }

    public void expectComplete() {
        if(!complete()){
            throw new IllegalStateException("This operation cannot be completed on an incomplete request");
        }
    }

    public boolean complete() {
        return over;
    }

    public void setCompleted(){
        this.over = true;
    }
}
