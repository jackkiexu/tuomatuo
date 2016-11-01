package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.message.Message;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Created by xjk on 2016/11/1.
 */
public class NewlineMessageFormatter implements MessageFormatter {

    public void writeTo(Message message, PrintStream output) {
        ByteBuffer buffer = message.payload();
        output.write(buffer.array(), buffer.arrayOffset(), buffer.limit());
        output.write('\n');
    }

    public void init(Properties props) {

    }

    public void close() {

    }
}
