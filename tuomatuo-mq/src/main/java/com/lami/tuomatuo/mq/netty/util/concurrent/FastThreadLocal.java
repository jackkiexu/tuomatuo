package com.lami.tuomatuo.mq.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;

/**
 * Created by xjk on 12/18/16.
 */
public class FastThreadLocal<V> {

    public static void removeAll(){

    }

    public final void remove(InternalThreadLocalMap threadLocalMap){

    }

    /**
     * Returns the initial value for this thread-local variable
     * @return
     * @throws Exception
     */
    protected V initialValue() throws Exception{
        return null;
    }

    protected void onRemoval(V value) throws Exception{

    }
}
