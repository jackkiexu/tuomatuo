package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * A subtype of {@link GenericFutureListener} that hides type parameter for convenience
 * <pre>
 *     Future f = new DefaultPromise(..);
 *     f.addListener(new FutureListener(){
 *         public void operationComplete(Future f) { .. }
 *     });
 * </pre>
 *
 * Created by xujiankang on 2016/12/14.
 */
public interface FutureListener<V> extends GenericFutureListener<Future<V>>{
}
