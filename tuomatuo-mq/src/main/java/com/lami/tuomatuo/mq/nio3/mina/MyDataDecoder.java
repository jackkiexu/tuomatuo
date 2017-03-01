package com.lami.tuomatuo.mq.nio3.mina;

/**
 * http://blog.csdn.net/cao478208248/article/details/41778511
 * http://blog.csdn.net/shiweijstl/article/details/25137543
 * http://www.blogjava.net/landon/archive/2013/12/02/407122.html
 * http://www.cnblogs.com/dagangzi/p/4717377.html
 */
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyDataDecoder extends CumulativeProtocolDecoder {

    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {

        if (in.remaining() < 4)
        {
            return false;
        }
        if (in.remaining() > 1) {
            in.mark();
            int len = in.getInt();

            if (len > in.remaining() - 2) {
                in.reset();
                return false;
            } else {
                in.reset();
                int sumLen = 6 + len;
                byte[] packArr = new byte[sumLen];
                in.get(packArr, 0, sumLen);
                IoBuffer buffer = IoBuffer.allocate(sumLen);
                buffer.put(packArr);
                buffer.flip();
                out.write(buffer);

                if (in.remaining() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}