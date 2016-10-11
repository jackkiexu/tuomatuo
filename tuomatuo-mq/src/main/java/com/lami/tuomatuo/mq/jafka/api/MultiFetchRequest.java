package com.lami.tuomatuo.mq.jafka.api;

import com.lami.tuomatuo.mq.jafka.network.Request;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/10/9.
 */
public class MultiFetchRequest implements Request{

    public List<FetchRequest> fetches;

    public MultiFetchRequest(List<FetchRequest> fetches) {
        this.fetches = fetches;
    }

    public RequestKeys getRequestKey() {
        return RequestKeys.MultiFetch;
    }

    public List<FetchRequest> getFetches() {
        return fetches;
    }



    public void writeTo(ByteBuffer buffer) {
        if(fetches.size() > Short.MAX_VALUE){
            throw new IllegalArgumentException("Number of requests in MultiFetch exceeds " + Short.MAX_VALUE);
        }

        buffer.putShort((short)fetches.size());
        for(FetchRequest fetch : fetches){
            fetch.writeTo(buffer);
        }
    }

    public int getSizeInBytes() {
        int size = 2;
        for(FetchRequest fetch : fetches){
            size += fetch.getSizeInBytes();
        }
        return size;
    }

    public static MultiFetchRequest readFrom(ByteBuffer buffer){
        int count = buffer.getShort();
        List<FetchRequest> fetches = new ArrayList<FetchRequest>(count);
        for(int i = 0; i < count; i++){
            fetches.add(FetchRequest.readFrom(buffer));
        }
        return new MultiFetchRequest(fetches);
    }
}
