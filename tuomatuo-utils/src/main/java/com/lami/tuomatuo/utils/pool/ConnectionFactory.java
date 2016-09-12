package com.lami.tuomatuo.utils.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by xujiankang on 2016/9/12.
 */
public class ConnectionFactory extends BasePoolableObjectFactory {

    private InetSocketAddress address;


    public ConnectionFactory(String ip, int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    @Override
    public Object makeObject() throws Exception {
        Socket socket = new Socket();
        socket.connect(address);
        return socket;
    }

    public void destroyObject(Object obj) throws Exception{
        if(obj instanceof Socket){
            ((Socket)obj).close();
        }
    }

    public boolean validateObject(Object obj) {
        if(obj instanceof Socket){
            Socket socket = (Socket)obj;
            if(!((Socket) obj).isConnected()){
                return false;
            }
            if(socket.isClosed()){
                return false;
            }
            return true;
        }
        return false;
    }
}
