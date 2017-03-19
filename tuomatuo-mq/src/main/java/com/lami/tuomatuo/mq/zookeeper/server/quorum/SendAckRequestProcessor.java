package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;

import java.io.Flushable;
import java.io.IOException;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class SendAckRequestProcessor implements RequestProcessor, Flushable {
    @Override
    public void flush() throws IOException {

    }
}
