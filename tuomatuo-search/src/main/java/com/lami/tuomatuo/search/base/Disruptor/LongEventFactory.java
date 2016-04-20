package com.lami.tuomatuo.search.base.Disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by xujiankang on 2016/4/20.
 */
public class LongEventFactory implements EventFactory{

    public Object newInstance() {
        return new LongEvent();
    }

}
