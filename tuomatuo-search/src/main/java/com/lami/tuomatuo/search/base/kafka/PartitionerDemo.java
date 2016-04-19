package com.lami.tuomatuo.search.base.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by xujiankang on 2016/4/19.
 */
public class PartitionerDemo implements Partitioner{

    public PartitionerDemo(VerifiableProperties verifiableProperties) {
    }

    public int partition(Object key, int numPartitions) {
        int partition = 0;
        if(key instanceof String){
            String keys = (String)key;
            int offset = ((String) key).lastIndexOf(".");
            if (offset > 0){
                partition = Integer.parseInt(keys.substring(offset + 1)) % numPartitions;
            }
        }else{
            partition = key.toString().length() % numPartitions;
        }
        return partition;
    }

}
