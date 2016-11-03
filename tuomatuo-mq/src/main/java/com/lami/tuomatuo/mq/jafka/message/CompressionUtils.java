package com.lami.tuomatuo.mq.jafka.message;

import com.lami.tuomatuo.mq.jafka.message.compress.CompressionFacade;
import com.lami.tuomatuo.mq.jafka.message.compress.CompressionFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/10/9.
 */
public class CompressionUtils {

    public static Message compress(Message[] messages, CompressionCodec compressionCodec){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressionCodec codec = compressionCodec;
        final CompressionFacade compressionFacade = CompressionFactory.create(
                codec == CompressionCodec.DefaultCompressionCodec?
                        CompressionCodec.GZIPCompressionCodec : codec,
                outputStream
        );

        ByteBuffer messageByteBuffer = ByteBuffer.allocate(MessageSet.messageSetSize(messages));
        for(Message message : messages){
            message.serializeTo(messageByteBuffer);
        }

        messageByteBuffer.rewind();
        try {
            compressionFacade.write(messageByteBuffer.array());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("writing data failed ", e);
        }finally {
            compressionFacade.close();
        }

        return new Message(outputStream.toByteArray(), compressionCodec);
    }

    public static ByteBufferMessageSet decompress(Message message){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new ByteBufferBackedInputStream(message.payload());

        byte[] intermediatebuffer = new byte[1024];
        CompressionCodec codec = message.compressionCodec();
        final CompressionFacade compressionFacade = CompressionFactory.create(
                codec == CompressionCodec.DefaultCompressionCodec ?
                        CompressionCodec.GZIPCompressionCodec : codec,
                inputStream
        );

        try {
            int dataRead = 0;
            while((dataRead = compressionFacade.read(intermediatebuffer)) > 0){
                outputStream.write(intermediatebuffer, 0, dataRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("decompression data failed", e);
        } finally {
            compressionFacade.close();
        }

        ByteBuffer outputBuffer = ByteBuffer.allocate(outputStream.size());
        outputBuffer.put(outputStream.toByteArray());
        outputBuffer.rewind();
        return new ByteBufferMessageSet(outputBuffer);
    }
}
