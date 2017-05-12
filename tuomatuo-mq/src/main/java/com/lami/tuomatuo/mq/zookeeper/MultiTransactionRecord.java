package com.lami.tuomatuo.mq.zookeeper;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import java.io.IOException;
import java.util.Iterator;

/**
 * Encodes a composite transaction. In the wire format, each transaction
 * consists of a single MultiHeader followed by the appropriate request
 * Each of these Multiheaders has a type which indicates
 * the type of the following transaction or negative number if no more transactions
 * are included
 *
 * Created by xujiankang on 2017/3/19.
 */
public class MultiTransactionRecord implements Record, Iterable<Op> {
    @Override
    public Iterator<Op> iterator() {
        return null;
    }

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {

    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {

    }
}
