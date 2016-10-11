package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.api.ICalculable;
import com.lami.tuomatuo.mq.jafka.api.RequestKeys;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/10/8.
 */
public interface Request extends ICalculable {

    RequestKeys getRequestKey();

    void writeTo(ByteBuffer buffer);
}
