package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.utils.IteratorTemplate;

import java.util.function.Consumer;

/**
 * Created by xjk on 2016/10/31.
 */
public class ConsumerIterator<T> extends IteratorTemplate<T> {
    @Override
    protected T makeNext() {
        return null;
    }

    public void forEachRemaining(Consumer<? super T> action) {

    }

    public void clearCurrentChunk(){

    }
}
