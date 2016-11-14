package com.lami.tuomatuo.search.base.classload;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * Created by xjk on 11/13/16.
 */
public class ClassLoaderTest {


    public static void main(String[] args) throws Exception{
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String filename = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(filename);
                    if(is == null){
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object obj = myLoader.loadClass("com.lami.tuomatuo.search.base.classload.ClassLoaderTest").newInstance();
        System.out.println("obj class : " + obj.getClass());
        System.out.println(obj instanceof com.lami.tuomatuo.search.base.classload.ClassLoaderTest);
    }

}
