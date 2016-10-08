package com.lami.tuomatuo.mq.jafka.network;

import java.util.Iterator;
import java.util.List;

/**
 * Created by xujiankang on 2016/10/8.
 */
public abstract class MultiSend<S extends Send> extends AbstractSend {

    protected int expectedBytesToWrite = 0;

    private int totalWritten = 0;

    private List<S> sends;

    private Iterator<S> iter;

    private S current;

    protected MultiSend() {
    }

    public MultiSend(List<S> sends) {
        this.sends = sends;
    }

    protected void setSends(List<S> sends){
        this.sends = sends;
        this.iter = sends.iterator();
        if(iter.hasNext()){
            this.current = iter.next();
        }
    }

    public List<S> getSends(){
        return sends;
    }

    public boolean complete(){

    }
}
