package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

/**
 * Created by xujiankang on 2017/6/23.
 */
public interface InstrumentableClassLoader {

    void addTransformer(ClassFileTransformer transformer);


    void removeTransformer(ClassFileTransformer transformer);

    ClassLoader copyWithoutTransformers();

}
