package com.lami.tuomatuo.search.base.concurrent.unsafe;

import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xujiankang on 2016/5/13.
 */
public class UnsafeTest {

    private static final Logger logger = Logger.getLogger(UnsafeTest.class);

    private static final Unsafe unsafe = UnSafeClass.getInstance();
    private static final Class<CustomerClass> customerClass = CustomerClass.class;
    private static final Field[] fields = customerClass.getDeclaredFields();

    private static void printFieldOffset(Field[] fields){
        System.out.println("-------------------------------------- Field Offset Begin --------------------");
        for(Field f : fields){
            System.out.println(f.getName() + " : " + unsafe.objectFieldOffset(f));
        }
        System.out.println("--------------------------------------- Field Offset End ----------------------");
    }

    public static void main2(String[] args) throws  Exception{
        printFieldOffset(fields);
        CustomerClass obj = new CustomerClass();

        long longFieldOffsetset = unsafe.objectFieldOffset(customerClass.getDeclaredField("longField"));

        logger.info("longFieldOffsetset : " + longFieldOffsetset);
        logger.info("longField by unsafe : " + unsafe.getLong(obj, longFieldOffsetset));

        /**
         * public final native boolean compareAndSwapLong(Object o, long offset, long expected, long newValue)
         */
        logger.info("change result:" + unsafe.compareAndSwapLong(obj, longFieldOffsetset, 0, 100));
        logger.info("longField by unsafe after update failed:" + unsafe.getLong(obj, longFieldOffsetset));

        logger.info("change result:" + unsafe.compareAndSwapLong(obj, longFieldOffsetset, 6, 100));
        logger.info("longField by unsafe after update successfully:" + unsafe.getLong(obj, longFieldOffsetset));
        unsafe.putLong(obj, longFieldOffsetset, 200);

        logger.info("longField by unsafe after put a new value: " + unsafe.getLong(obj, longFieldOffsetset));
    }


    public static void main(String[] args) {
        logger.info("start");
        LockSupport.parkNanos(1000000000);
        logger.info("end");

        // 一开始会 block 线程, 知道给定时间过去后 才会往下走

        logger.info("start");
        LockSupport.unpark(Thread.currentThread());
        LockSupport.parkNanos(1000000000);
        logger.info("end");

        logger.info("start");
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());
        LockSupport.parkNanos(1000000000);
        logger.info("inner");
        LockSupport.parkNanos(1000000000);
        logger.info("end");
    }

}
