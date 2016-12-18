package com.lami.tuomatuo.mq.netty.util.internal;

import com.lami.tuomatuo.mq.netty.util.concurrent.Promise;
import com.lami.tuomatuo.mq.netty.util.internal.logging.InternalLogger;

/**
 * Internal utilities to notify {@link com.lami.tuomatuo.mq.netty.util.concurrent.Promise}
 * Created by xjk on 12/18/16.
 */
public class PromiseNotificationUtil {

    public PromiseNotificationUtil() {
    }

    /**
     * Try to cancel the {@link Promise} and log if {@code logger} is not {@code null} in case this fails
     * @param p
     * @param logger
     */
    public static void tryCancel(Promise<?> p, InternalLogger logger){
        if(!p.cancel(false) && logger != null){
            Throwable err = p.cause();
            if(err == null){
                logger.warn("Failed to cancel promise because it has successed already: {}", p);
            }else{
                logger.warn("Failed to cancel promise because it has failed already : {} unnotified cause: ", p, err);
            }
        }
    }

    public static <V> void trySuccess(Promise<? super V> p, V result, InternalLogger logger){
        if(!p.trySuccess(result) && logger != null){
            Throwable err = p.cause();
            if(err == null){
                logger.warn("Failed to mark a promise as success because it has successed already: {}", p);
            }else{
                logger.warn("Failed to mark a promise as success because it has failed already: {}, unnotified cause: ", p, err);
            }
        }
    }

    public static void tryFailure(Promise<?> p, Throwable cause, InternalLogger logger){
        if(!p.tryFailure(cause) && logger != null){
            Throwable err = p.cause();
            if(err == null){
                logger.warn("Failed to mark a promise as failure because it has successed already: {}", p, cause);
            }else{
                logger.warn("Failed to mark a promise as failure because it has failed already: {}, unnotified cause: {}", p,
                        ThrowableUtil.stackTraceToString(err), cause);
            }
        }
    }

}
