package com.lami.tuomatuo.mq.jafka.message;

import com.sohu.jafka.common.UnKnownCodecException;

/**
 * message compression method
 *
 * Created by xjk on 9/25/16.
 */
public enum CompressionCodec {

    NoCompressionCodec(0),
    GZIPCompressionCodec(1),
    SnappyCompressionCodec(2),
    DefaultCompressionCodec(1);

    public int codec;

    CompressionCodec(int codec) {
        this.codec = codec;
    }

    public static CompressionCodec valueof(int codec){
        switch (codec){
            case 0:
                return NoCompressionCodec;
            case 1:
                return GZIPCompressionCodec;
            case 2:
                return SnappyCompressionCodec;
        }
        throw new UnKnownCodecException("unknow codec: " + codec);
    }
}
