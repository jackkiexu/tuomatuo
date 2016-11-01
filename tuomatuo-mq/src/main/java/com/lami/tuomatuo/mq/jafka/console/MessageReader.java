package com.lami.tuomatuo.mq.jafka.console;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by xjk on 2016/11/1.
 */
public interface MessageReader extends Closeable {

    void init(InputStream inputStream, Properties props);

    String readMessage() throws IOException;

}
