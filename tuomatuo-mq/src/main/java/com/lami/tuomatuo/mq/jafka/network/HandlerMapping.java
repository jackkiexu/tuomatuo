package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.api.RequestKeys;

/**
 * Created by xjk on 10/1/16.
 */
public interface HandlerMapping {

    Send handler(RequestKeys requestType, Receive request);

}
