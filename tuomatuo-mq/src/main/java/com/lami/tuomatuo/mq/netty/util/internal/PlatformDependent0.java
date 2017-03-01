package com.lami.tuomatuo.mq.netty.util.internal;

import com.lami.tuomatuo.utils.UnSafeClass;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The {@link PlatformDependent} operation which requires access to {@code sum.misc.*}
 *
 * Created by xjk on 12/15/16.
 */
public class PlatformDependent0 {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
    static  Unsafe UNSAFE;
    private static long ADDRESS_FIELD_OFFSET;
    private static long BYTE_ARRAY_BASE_OFFSET;
    private static Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;

    /**
     * Limits the number of bytes to copy per {@link Unsafe#copyMemory(long, long, long)} to allow safepoint polling
     * during a large copy
     */
    private static final long UNSAFE_COPY_THRESHOLD = 1024L * 1024L;

    private static boolean UNALIGNED;

    static {
        final ByteBuffer direct = ByteBuffer.allocateDirect(1);
        Field addressField;
        // attempt to access field Buffer#address
        final Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field field = Buffer.class.getDeclaredField("address");
                    field.setAccessible(true);
                    // if direct really is a direct buffer, address will be non-zero
                    if(field.getLong(direct) == 0){
                        return null;
                    }
                    return field;
                } catch (NoSuchFieldException e) {
                    return e;
                } catch (IllegalAccessException e) {
                    return e;
                }
            }
        });

        if(maybeAddressField instanceof Field){
            addressField = (Field)maybeAddressField;
            logger.debug("java.nio.Buffer.address : available");
        }else{
            logger.info("java.nio.Buffer.address: unavailable", (Exception) maybeAddressField);
            addressField = null;
        }

        Unsafe unsafe;
        if(addressField != null){
            // attempt to access field Unsafe#theUnsafe
            final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                /*    try {*/
                        return  UnSafeClass.getInstance();
                        /*final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                        unsafeField.setAccessible(true);
                        // the unsafe instance
                        return unsafeField.get(null);*/
                   /* } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        return e;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return e;
                    }*/
                }
            });

        }


    }


    static ClassLoader getClassLoader(final Class<?> clazz){
        if(System.getSecurityManager() == null){
            return clazz.getClassLoader();
        }else{
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            });
        }
    }

    static ClassLoader getContextClassLoader(){
        if(System.getSecurityManager() == null){
            return Thread.currentThread().getContextClassLoader();
        }else{
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
    }

    static ClassLoader getSystemClassLoader(){
        if(System.getSecurityManager() == null){
            return ClassLoader.getSystemClassLoader();
        }else{
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return ClassLoader.getSystemClassLoader();
                }
            });
        }
    }

    private PlatformDependent0(){}
}
