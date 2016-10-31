package com.lami.tuomatuo.mq.nio.client;

import org.apache.log4j.Logger;

/**
 * Created by xjk on 10/29/16.
 */
public class RspHandler {

    private static final Logger logger = Logger.getLogger(RspHandler.class);

    private byte[] rsp = null;

    public synchronized boolean handleResponse(byte[] rsp){
        this.rsp = rsp;
        this.notify();
        return true;
    }

    public synchronized void waitForResponse(){
        while(this.rsp == null){
            try{
                this.wait();
            }catch (Exception e){

            }
        }
        logger.info(new String(this.rsp));

    }
}
