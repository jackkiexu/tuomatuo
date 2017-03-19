package com.lami.tuomatuo.mq.zookeeper.server;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import java.io.IOException;

/**
 * This class contains the data for a node in the data tree
 * A data node contains a reference to its parent, a byte array as it data, an
 * array of ACLs, a stat object, and a set of its children's paths
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DataNode implements Record {

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {

    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {

    }
}
