package com.lami.tuomatuo.mq.zookeeper.server.persistence;

import org.apache.jute.Record;
import org.apache.zookeeper.txn.Txn;
import org.apache.zookeeper.txn.TxnHeader;

import java.io.IOException;

/**
 * Interface for reading transaction logs
 * Created by xjk on 3/18/17.
 */
public interface TxnLog {

    /**
     * roll the current log being appended to
     * @throws IOException
     */
    void rollLog() throws IOException;

    /**
     * Append a request to the transaction log
     *
     * @param hdr the transaction header
     * @param r the transcation header
     * @return true if something appended, otw false
     * @throws IOException
     */
    boolean append(TxnHeader hdr, Record r) throws IOException;

    /**
     * Start reading the transaction logs from a given zxid
     *
     * @param zxid
     * @return an iterator to read the next transaction in the logs
     * @throws IOException
     */
    org.apache.zookeeper.server.persistence.TxnLog.TxnIterator read(long zxid) throws IOException;

    /**
     * The last zxid of the logged transactions
     * @return the last zxid of the logged transactions
     * @throws IOException
     */
    long getLastLoggedZxid() throws IOException;

    /**
     * truncate the log to get in sync with the
     * leader
     * @param zxid the zxid to truncate at
     * @return
     * @throws IOException
     */
    boolean truncate(long zxid) throws IOException;

    /**
     * the dbid for this transaction log
     * @return the dbid for this transaction log
     * @throws IOException
     */
    long getDbId() throws IOException;

    /**
     * commit the trasaction and make sure
     * they are persisted
     * @throws IOException
     */
    void commit() throws IOException;

    /**
     * close the transactions logs
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * an iterating interface for reading
     * transaction logs
     */
    public interface TxnIterator{
        /**
         * Return the transaction header
         * @return return the transaction header
         */
        TxnHeader getHeader();

        /**
         * return the transaction record
         * @return
         */
        Record getTxn();

        /**
         * go to the next transaction record
         * @return
         * @throws IOException
         */
        boolean next() throws IOException;

        /**
         * close files and release the resources
         * @throws IOException
         */
        void close() throws IOException;

        /**
         * Get an estimated storage space used to store transaction records
         * that will return by this iterator
         * @return
         * @throws IOException
         */
        long getStorageSize() throws IOException;
    }
}
