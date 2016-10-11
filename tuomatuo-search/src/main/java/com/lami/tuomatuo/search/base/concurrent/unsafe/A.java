package com.lami.tuomatuo.search.base.concurrent.unsafe;

import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;

/**
 * Created by xjk on 2016/5/12.
 */
public class A {
    private long a; // not initialized value

    public A() {
        this.a = 1;
    }

    public long a() {return this.a;}

    /**
     *  get unsafe instance
     * @return
     */
    public static Unsafe getUnsafe(){
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return unsafe;
    }

    /**
     * get the memory address of the method
     * @param o
     * @return
     */
    public static long sizeOfMomery(Object o){
        Unsafe u = getUnsafe();
        HashSet<Field> fields = new HashSet<Field>();
        Class c = o.getClass();
        while(c != Object.class){
            for(Field f : c.getDeclaredFields()){
                if((f.getModifiers() & Modifier.STATIC) == 0){
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }

        // get offset
        long maxSize = 0;
        for(Field f : fields){
            long offset = u.objectFieldOffset(f);
            if(offset > maxSize){
                maxSize = offset;
            }
        }

        return ((maxSize / 8) + 1) * 8;
    }

    public Class getAnyClass(){
        try {
            byte[] classContents = getClassContent();

            /*Class c = getUnsafe().defineClass(
              null, classContents, 0, classContents.length
            );
            c.getMethod("a").invoke(c.newInstance(), null);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getClassContent() throws Exception{
        File f = new File("/home/mishadoff/tmp/A.class");
        FileInputStream fileInputStream = new FileInputStream(f);
        byte[] content = new byte[(int)f.length()];
        fileInputStream.read(content);
        fileInputStream.close();
        return content;
    }

    /**
     * just read size value from the class struct for this object, which located with offset 12 in JVM 1.7 32 bit
     * @param object
     * @return
     */
    public static long sizeOf(Object object){
        return getUnsafe().getAddress(
          normalize(getUnsafe().getInt(object, 4L)) + 12L
        );
    }

    /**
     * clone object base copy content in memory
     * @param obj
     * @return
     */
    static Object shallowCopy(Object obj){
        long size = sizeOf(obj);
        long start = toAddress(obj);
        long address = getUnsafe().allocateMemory(size);
        getUnsafe().copyMemory(start, address, size);
        return fromAddress(address);
    }

    /**
     * convert object to its addreaa in memory and vice versa
     * @param obj
     * @return
     */
    static long toAddress(Object obj){
        Object[] array = new Object[]{obj};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        return normalize(getUnsafe().getInt(array, baseOffset));
    }

    static Object fromAddress(long address){
        Object[] array = new Object[]{null};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        getUnsafe().putLong(array, baseOffset, address);
        return array[0];
    }



    /**
     * a method for casting signed into unsigned long, for correct address uage
     * @param value
     * @return
     */
    public static long normalize(int value){
        if(value > 0) return value;
        return (~0L >>> 32) & value;
    }

    public static void main(String[] args) {
        System.out.println(normalize(-1));
    }


    /**
     * Big arrays
     * As you know Integer.MAX_VALUE constant is a mix size of java array. Using direct memory. Using direct memory allocation we can create arrays with size limited by only heap size
     * @param args
     * @throws Exception
     */
    class SuperArray{
        private final static int BYTE = 1;

        private long size;
        private long address;

        public SuperArray(long size) {
            this.size = size;
            address = getUnsafe().allocateMemory(size * BYTE);
        }

        public void set(long i, byte value){
            getUnsafe().putByte(address + i * BYTE, value);
        }

        public int get(long idx){
            return getUnsafe().getByte(address + idx * BYTE);
        }

        public long size(){
            return  size;
        }
    }


    public static void main1(String[] args) throws Exception{
        A o1 = new A();
        System.out.println(o1.a());

        A o2 = A.class.newInstance();
        System.out.println(o2.a());


        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        A o3 = (A)unsafe.allocateInstance(A.class);
        System.out.println(o3.a());
    }


}
