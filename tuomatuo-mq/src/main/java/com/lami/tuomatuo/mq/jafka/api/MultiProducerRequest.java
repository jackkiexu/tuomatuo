package com.lami.tuomatuo.mq.jafka.api;

import com.lami.tuomatuo.mq.jafka.network.Request;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Multi-Message (may be without same topic)
 * Created by xjk on 2016/11/4.
 */
public class MultiProducerRequest implements Request {

    public List<ProducerRequest> producers;

    public MultiProducerRequest(List<ProducerRequest> produces){
        this.producers = produces;
    }

    public RequestKeys getRequestKey() {
        return RequestKeys.MultiProduce;
    }

    public void writeTo(ByteBuffer buffer) {
        if(producers.size() > Short.MAX_VALUE){
            throw new IllegalArgumentException("Number of requests in MultiFetchRequest exceeds " + Short.MAX_VALUE + " .");
        }
        buffer.putShort((short)producers.size());
        for(ProducerRequest produce : producers){
            produce.writeTo(buffer);
        }
    }

    public int getSizeInBytes() {
        int size = 2;
        for(ProducerRequest produce : producers){
            size += produce.getSizeInBytes();
        }
        return size;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for(ProducerRequest produce : producers){
            buf.append(produce.toString()).append(",");
        }
        return buf.toString();
    }

    public static MultiProducerRequest readFrom(ByteBuffer buffer){
        int count = buffer.getShort();
        List<ProducerRequest> produces = new ArrayList<ProducerRequest>();
        for(int i = 0; i < count; i++){
            produces.add(ProducerRequest.readFrom(buffer));
        }
        return new MultiProducerRequest(produces);
    }
}
