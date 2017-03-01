package com.lami.tuomatuo.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 *
 * Unsafe 类是java中的保护类, 所以就通过这种方式获取(ps 也可以在命令行中指定所加载的包是受保护的)
 * Created by xjk on 2016/5/13.
 */
public class UnSafeClass {

    private static Unsafe unsafe;

    static{
        try {
            // 通过反射的方式获取unsafe类
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Unsafe getInstance(){
        return unsafe;
    }
}
