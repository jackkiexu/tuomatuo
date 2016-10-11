package com.lami.tuomatuo.mq.jafka.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xjk on 2016/10/8.
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
        if(current != null) return false;
        if(totalWritten != expectedBytesToWrite){
            logger.info("mismatch in sending bytes over socket; expected: " + expectedBytesToWrite + " actual: " + totalWritten);
        }
        return true;
    }

    public int writeTo(GatheringByteChannel channel) throws IOException {
        expectIncomplete();
        int written = current.writeTo(channel);
        totalWritten += written;
        if(current.complete()){ // move to the next element while current element is finished writting
            current = iter.hasNext() ? iter.next() : null;
        }
        return written;
    }
}
