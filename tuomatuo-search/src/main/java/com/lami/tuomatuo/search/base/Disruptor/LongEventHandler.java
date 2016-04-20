package com.lami.tuomatuo.search.base.Disruptor;


import com.lmax.disruptor.EventHandler;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/4/20.
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    private static final Logger logger = Logger.getLogger(LongEventHandler.class);

    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        logger.info("LongEventHandler : " + longEvent.getValue() + ", l:" + l + ", b:"+ b);
    }
}
