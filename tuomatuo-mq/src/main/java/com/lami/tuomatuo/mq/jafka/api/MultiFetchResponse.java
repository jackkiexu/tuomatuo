package com.lami.tuomatuo.mq.jafka.api;


import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by xjk on 2016/10/9.
 */
public class MultiFetchResponse  implements Iterable<ByteBufferMessageSet>{

    private Collection<ByteBufferMessageSet> messageSets;

    public MultiFetchResponse(ByteBuffer buffer, int numSets, List<Long> offsets) {
        super();
        this.messageSets = new ArrayList<ByteBufferMessageSet>();
        for(int i = 0; i < numSets; i++){
            int size = buffer.getInt();
            short errorCode = buffer.getShort();
            ByteBuffer copy = buffer.slice();
            int payloadSize = size - 2;
            copy.limit(payloadSize);
            buffer.position(buffer.position() + payloadSize);
            messageSets.add(new ByteBufferMessageSet(copy, offsets.get(i), ErrorMapping.valueOf(errorCode)));
        }
    }

    public Iterator<ByteBufferMessageSet> iterator() {
        return messageSets.iterator();
    }

}
