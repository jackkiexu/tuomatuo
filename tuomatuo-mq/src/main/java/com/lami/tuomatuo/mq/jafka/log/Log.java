package com.lami.tuomatuo.mq.jafka.log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xjk on 2016/10/9.
 */
public class Log implements Closeable {

    public String name;






    public long size(){
        return 0l;
    }

    public int getNumberOfSegments(){
        return 0;
    }

    public long getHightwaterMark(){
        return 0l;
    }





    public void close() throws IOException {

    }


}
