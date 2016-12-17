package com.lami.tuomatuo.mq.netty.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by xjk on 12/17/16.
 */
public class ThrowableUtil {

    public ThrowableUtil() {
    }

    /**
     * Set the {@link StackTraceElement} for the given {@link Throwable}, using the {@link Class} and method name
     * @param cause
     * @param clazz
     * @param method
     * @param <T>
     * @return
     */
    public static <T extends Throwable> T unknowStackTrace(T cause, Class<?> clazz, String method){
        cause.setStackTrace(new StackTraceElement[]{new StackTraceElement(clazz.getName(), method, null, -1)});
        return cause;
    }

    public static String stackTraceToString(Throwable cause){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        cause.printStackTrace(pout);
        pout.flush();

        try {
            return new String(out.toByteArray());
        } finally {
            try{
                out.close();
            }catch (Exception e){

            }
        }
    }
}
