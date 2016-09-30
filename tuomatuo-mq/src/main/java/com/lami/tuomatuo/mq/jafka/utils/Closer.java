package com.lami.tuomatuo.mq.jafka.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.Selector;

/**
 * an useful tools to close some atreams or file descriptions
 *
 * Created by xujiankang on 2016/9/30.
 */
public class Closer {

    private static final Logger logger = Logger.getLogger(Closer.class);

    public static void close(java.io.Closeable closeable) throws IOException{
        close(closeable, logger);
    }

    public static void close(java.io.Closeable closeable, Logger logger) throws IOException{
        if(closeable == null) return;;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    public static void closeQuietly(Selector selector){
        closeQuietly(selector, logger);
    }

    public static void closeQuietly(java.io.Closeable closeable, Logger logger){
        if(closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    public static void closeQuietly(Socket socket){
        if(socket == null){
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }

    public static void closeQuietly(java.io.Closeable closeable){
        closeQuietly(closeable, logger);
    }

}
