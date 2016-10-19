package com.lami.tuomatuo.mq.jafka.utils.zookeeper;

import com.github.zkclient.exception.ZkMarshallingError;
import com.github.zkclient.serialize.ZkSerializer;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

/**
 * Created by xjk on 2016/10/19.
 */
public class ZkStringSerializer implements ZkSerializer {

    private static final ZkStringSerializer instance = new ZkStringSerializer();

    public byte[] serialize(Object data) throws ZkMarshallingError {
        if(data == null){
            throw new NullPointerException();
        }
        return Utils.getBytes((String)data);
    }

    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return bytes == null ? null : Utils.fromBytes(bytes);
    }

    public static ZkStringSerializer getInstance(){
        return instance;
    }
}
