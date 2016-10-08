package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.List;

/**
 * Created by xujiankang on 2016/10/8.
 */
public class OffsetArraySend extends AbstractSend {

    ByteBuffer header = ByteBuffer.allocate(6);
    ByteBuffer contentBuffer;

    public OffsetArraySend(List<Long> offsets) {
        header.putInt(4 + offsets.size() * 8 + 2);
        header.putShort(ErrorMapping.NoError.code);
        header.rewind();
        contentBuffer = OffsetRequest.serializeOffsetArray(offsets);
    }

    public int writeTo(GatheringByteChannel channel) throws IOException {
        expectComplete();
        int written = 0;
        if(header.hasRemaining()){
            written += channel.write(header);
        }
        if(!header.hasRemaining() && contentBuffer.hasRemaining()){
            written += channel.write(contentBuffer);
        }
        if(!contentBuffer.hasRemaining()){
            setCompleted();
        }
        return written;
    }
}
