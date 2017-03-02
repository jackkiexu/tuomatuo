package com.apache.catalina.tribes.transport.nio;

import com.apache.catalina.tribes.transport.AbstractSender;
import com.apache.catalina.tribes.transport.io.XByteBuffer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * This class is NOT thread safe and should never be used with more than one thread at a time
 *
 * This is a state machine, handled by the process method
 * States are:
 * - NOT_CONNECTED -&gt; connect() -&gt: CONNECTED (&gt >)
 * - CONNECTED -&gt: setMessage() -&gt: READY to WRITE
 * - READY_TO_WRITE -&gt: write() -&gt: READY TO WRITE | READY TO READ
 * - READY_TO_READ -&gt: read() -&gt: READY_TO_READ | TRANSFER_COMPLETE
 * - TRANSFER_COMMPLETE -&gt: CONNECTED
 *
 * Created by xujiankang on 2017/3/2.
 */
public class NioSender extends AbstractSender{

    private static final Logger logger =  Logger.getLogger(NioSender.class);

    protected Selector selector;
    protected SocketChannel socketChannel = null;
    protected DatagramChannel dataChannel = null;

    /**
     * STATE VARIABLES
     */
    protected ByteBuffer readbuf = null;
    protected ByteBuffer writebuf = null;
    protected volatile byte[] current = null;
    protected final XByteBuffer ackbuf = new XByteBuffer(128, true);
    protected int remaining = 0;
    protected boolean complete;

    protected boolean connecting = false;

    public NioSender() {
        super();
    }

    @Override
    public synchronized void connect() throws IOException {

    }

    @Override
    public void disconnect() {
        try{
            connecting = false;
            setConnected(false);
            if(socketChannel != null){
                try{
                    try{
                        socketChannel.socket().close();
                    }catch (Exception x){}
                    try{
                        socketChannel.close();
                    }catch (Exception x){}

                }finally {
                    socketChannel = null;
                }
            }

            if(dataChannel != null){
                try{
                    try{
                        dataChannel.socket().close();
                    }catch (Exception x){}

                    try{
                        dataChannel.close();
                    }catch (Exception x){}
                }finally {
                    dataChannel = null;
                }
            }
        }catch (Exception x){
            logger.info("nioSender. unable. disconnect");
        }
    }

    @Override
    public int getRequestCout() {
        return 0;
    }

    private void configureSocket() throws IOException{
        if(socketChannel != null){
            socketChannel.configureBlocking(false);
            socketChannel.socket().setSendBufferSize(getTxBufSize());
        }
    }

    private ByteBuffer getReadBuffer(){
        return getBuffer(getRxBufSize());
    }

    private ByteBuffer getWriteBuffer(){
        return getBuffer(getTxBufSize());
    }

    private ByteBuffer getBuffer(int size){
        return (getDirectBuffer() ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size));
    }

    public void reset(){
        if(isConnected() && readbuf == null){
            readbuf = getReadBuffer();
        }
        if(readbuf != null) readbuf.clear();
        if(writebuf != null) writebuf.clear();
        current = null;
        ackbuf.clear();
        remaining = 0;
        complete = false;
        setAttempt(0);
        setUdpBased(false);
    }
}
