package com.lami.tuomatuo.mq.jafka.message.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by xjk on 2016/10/9.
 */
public class GZIPCompression extends CompressionFacade{

    public GZIPCompression(InputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
        super(inputStream != null? new GZIPInputStream(inputStream) : null,
                outputStream != null ? new GZIPOutputStream(outputStream) : null
                );
    }
}
