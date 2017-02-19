package com.lami.tuomatuo.mq.nio3.mina;


/**
 * Created by admin on 2016/12/22.
 */
public class ConnectionConfig {

    private String ip;
    private int port;
    private int readBufferSize;
    private long connectionTimeout;


    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public static class Builder{
        private String ip = "192.168.0.1";
        private int port = 8888;
        private int readBufferSize = 1024;
        private long connectionTimeout = 10000;


        public Builder setIp(String ip){
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port){
            this.port = port;
            return this;
        }

        public Builder setReadBufferSize(int readBufferSize){
            this.readBufferSize = readBufferSize;
            return this;
        }

        public Builder setConnectionTimeout(long connectionTimeout){
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        private void applyConfig(ConnectionConfig config){

            config.ip = this.ip;
            config.port = this.port;
            config.readBufferSize = this.readBufferSize;
            config.connectionTimeout = this.connectionTimeout;
        }

        public ConnectionConfig builder(){
            ConnectionConfig config = new ConnectionConfig();
            applyConfig(config);
            return config;
        }
    }
}
