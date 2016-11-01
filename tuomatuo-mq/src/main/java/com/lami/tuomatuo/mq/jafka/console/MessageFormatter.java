package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.message.Message;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Created by xjk on 2016/11/1.
 */
public interface MessageFormatter extends Closeable {

    void writeTo(Message message, PrintStream output);

    void init(Properties props);

    void close();

}
