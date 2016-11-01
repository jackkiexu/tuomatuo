package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.message.Message;

import java.io.PrintStream;
import java.util.Properties;

/**
 * Created by xjk on 2016/11/1.
 */
public class ChecksumMessageFormatter implements MessageFormatter{

    private String topicStr = null;

    public void writeTo(Message message, PrintStream output) {
        output.println(topicStr + " checksum " + message.checksum());
    }

    public void init(Properties props) {
        topicStr = props.getProperty("topic");
        topicStr = topicStr == null ? "" : topicStr + "-";
    }

    public void close() {

    }
}
