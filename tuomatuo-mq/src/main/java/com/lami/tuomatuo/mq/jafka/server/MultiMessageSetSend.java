package com.lami.tuomatuo.mq.jafka.server;

import com.lami.tuomatuo.mq.jafka.common.annotations.NotThreadSafe;
import com.lami.tuomatuo.mq.jafka.network.ByteBufferSend;
import com.lami.tuomatuo.mq.jafka.network.MessageSetSend;
import com.lami.tuomatuo.mq.jafka.network.MultiSend;
import com.lami.tuomatuo.mq.jafka.network.Send;

import java.util.ArrayList;
import java.util.List;

/**
 * A set message sets prefixed by size
 *
 * Created by xjk on 10/31/16.
 */
@NotThreadSafe
public class MultiMessageSetSend extends MultiSend<Send>{

    public MultiMessageSetSend(List<MessageSetSend> sets) {
        super();
        final ByteBufferSend sizeBuffer = new ByteBufferSend(6);
        List<Send> sends = new ArrayList<Send>(sets.size() + 1);
        sends.add(sizeBuffer);
        int allMessageSetSize = 0;
        for(MessageSetSend send : sets){
            sends.add(send);
            allMessageSetSize += send.getSendSize();
        }

        // write head size
        sizeBuffer.getBuffers().putInt(2 + allMessageSetSize); // 4
        sizeBuffer.getBuffers().putShort((short) 0); // 2
        sizeBuffer.getBuffers().rewind();
        super.expectedBytesToWrite = 4 + 2 + allMessageSetSize;

        super.setSends(sends);
    }
}
