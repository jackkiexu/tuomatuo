package com.lami.tuomatuo.mq.nio;

import org.apache.log4j.Logger;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xjk on 2016/10/9.
 */
public class TestFile {

    private static final Logger logger = Logger.getLogger(TestFile.class);

    public static void main(String[] args) throws Exception{
        RandomAccessFile aFile=new RandomAccessFile("D:/BugReport.txt","rw");
        FileChannel inChannel=aFile.getChannel();

        /*分配buffer  */
        ByteBuffer buf= ByteBuffer.allocate(2);
        /*读入到buffer*/
        int bytesRead=inChannel.read(buf);
        logger.info("bytesRead:"+bytesRead);
        while(bytesRead!=-1)
        {
            /*设置读*/
            buf.flip();
            /*开始读取*/
            while(buf.hasRemaining())
            {
                logger.info((char) buf.get());
            }
            buf.clear();
            bytesRead=inChannel.read(buf);
        }

        aFile.close();
    }
}
