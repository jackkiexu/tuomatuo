package com.lami.tuomatuo.mq.jafka.cluster;

/**
 * Created by xjk on 2016/10/9.
 */
public class Broker {

    public int id;

    public String creatorId;

    public String host;

    public int port;

    public Broker(int id, String creatorId, String host, int port) {
        super();
        this.id = id;
        this.creatorId = creatorId;
        this.host = host;
        this.port = port;
    }

    public String getZKString(){
        return creatorId + ":" + host + ":" + port;
    }

    @Override
    public String toString() {
        return "Broker{" +
                "id=" + id +
                ", creatorId='" + creatorId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((host == null)?0:host.hashCode());
        result = prime * result + id;
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Broker other = (Broker)obj;
        if(host == null){
            if(other.host != null) return false;
        }else if(!host.equals(other.host)) return false;
        if(id != other.id){
            return false;
        }
        if(port != other.port){
            return false;
        }
        return true;
    }

    public static Broker createBroker(int id, String brokerInfoString){
        String[] brokerInfo = brokerInfoString.split(":");
        return new Broker(id, brokerInfo[0], brokerInfo[1], Integer.parseInt(brokerInfo[2]));
    }
}
