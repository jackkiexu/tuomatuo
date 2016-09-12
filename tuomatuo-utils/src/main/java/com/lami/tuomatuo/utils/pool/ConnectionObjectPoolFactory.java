package com.lami.tuomatuo.utils.pool;

import org.apache.commons.pool.impl.GenericObjectPool;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by xujiankang on 2016/9/12.
 */
public class ConnectionObjectPoolFactory {

    private GenericObjectPool pool;

    public ConnectionObjectPoolFactory(GenericObjectPool.Config config, String ip, int port) {
        ConnectionFactory factory = new ConnectionFactory(ip, port);
        pool = new GenericObjectPool(factory, config);
    }

    public Socket getConnection() throws Exception{
        return (Socket)pool.borrowObject();
    }

    public void releaseConnection(Socket socket){
        try{
            pool.returnObject(socket);
        }catch (Exception e){
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e1) {

                }
            }
        }
    }

    public static void main(String[] args) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 16;
        config.maxWait = 30000;
        config.testOnBorrow = true;
        config.testOnReturn = true;
        ConnectionObjectPoolFactory poolFactory = new ConnectionObjectPoolFactory(config, "192.168.1.28", 8130);
        Socket socket = null ;
        try{
            socket = poolFactory.getConnection();
            ////
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(socket != null){
                poolFactory.releaseConnection(socket);
            }
        }
    }

}
