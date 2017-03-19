package com.lami.tuomatuo.mq.zookeeper;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import java.io.IOException;
import java.util.Iterator;

/**
 * Handles the response from a multi request. Such a response consist of
 * a sequence of response each prefixed by a multiresponse that indicates
 * the type of the response . The end of the list is indicated by a MultiHeader
 * with a negative type. Each individual response is in the same format as
 * with the corresponding operation in the original request list
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Multiresponse implements Record, Iterable<OpResult> {
    @Override
    public Iterator<OpResult> iterator() {
        return null;
    }

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {

    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {

    }
}
