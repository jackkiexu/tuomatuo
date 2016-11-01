package com.lami.tuomatuo.mq.jafka.console;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Created by xjk on 2016/11/1.
 */
public class LineMessageReader implements MessageReader {

    private static final Logger logger = Logger.getLogger(LineMessageReader.class);

    private BufferedReader reader;
    boolean first = true;

    public void init(InputStream inputStream, Properties props) {
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() throws IOException {
        if(first){
            first = false;
            logger.info("Entry you message and exit with empty string");
        }
        logger.info(">");
        return reader.readLine();
    }

    public void close() throws IOException {

    }
}
