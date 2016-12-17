package com.lami.tuomatuo.mq.netty.util.internal;

/**
 * A grab-bag of useful utility methods
 *
 * Created by xjk on 12/17/16.
 */
public class ObjectUtil {

    public ObjectUtil() {
    }

    /**
     * Checks that given argument is not null, If it is, throws {@link NullPointerException}.
     * Otherwise, returns the argument
     * @param arg
     * @param text
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T arg, String text){
        if(arg == null){
            throw new NullPointerException(text);
        }
        return arg;
    }


}
