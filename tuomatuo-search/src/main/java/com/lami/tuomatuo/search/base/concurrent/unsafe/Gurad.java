package com.lami.tuomatuo.search.base.concurrent.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by xujiankang on 2016/5/12.
 */
public class Gurad {

    private int ACCESS_ALLOWED = 1;

    public boolean giveAccess(){
        return 42 == ACCESS_ALLOWED;
    }




    public static void main(String[] args) throws Exception{
        Gurad gurad = new Gurad();
        System.out.println(gurad.giveAccess());

        Unsafe unsafe = A.getUnsafe();
        Field f = gurad.getClass().getDeclaredField("ACCESS_ALLOWED");
        unsafe.putInt(gurad, unsafe.objectFieldOffset(f), 42);

        System.out.println(gurad.giveAccess());
    }
}
