package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.message.compress.CompressionFacade;

import java.io.ByteArrayOutputStream;

/**
 * Created by xujiankang on 2016/10/9.
 */
public class CompressionUtils {

    public static Message compress(Message[] messages, CompressionCodec compressionCodec){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressionCodec codec = compressionCodec;


        return null;
    }
}
