package com.lami.tuomatuo.mq.jafka.message.compress;

import com.lami.tuomatuo.mq.jafka.common.UnKnownCodecException;
import com.lami.tuomatuo.mq.jafka.message.CompressionCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xjk on 2016/10/9.
 */
public class CompressionFactory  {

    public static CompressionFacade create(CompressionCodec compressionCodec, ByteArrayOutputStream out){
        return create(compressionCodec, null, out);
    }

    public static CompressionFacade create(CompressionCodec compressionCodec, InputStream in){
        return create(compressionCodec, in, null);
    }

    private static CompressionFacade create(CompressionCodec compressionCodec, InputStream in, ByteArrayOutputStream out){
        try {
            switch(compressionCodec){
                case GZIPCompressionCodec:
                    return new GZIPCompression(in, out);
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        throw new UnKnownCodecException("Unknow Codec : " + compressionCodec);
    }
}
