package com.lami.tuomatuo.mq.zookeeper.server.persistence;

import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * A collection of utility methods for dealing with file name parsing
 * low level I/O operations and marshalling/unmarshalling
 * Created by xjk on 3/18/17.
 */
public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);
    private static final String SNAP_DIR = "snapDir";
    private static final String LOG_DIR = "logDir";
    private static final String DB_FORMAT_CONV = "dbFormatConversion";
    private static final ByteBuffer fill = ByteBuffer.allocateDirect(1);


    public static String makeURIString(String dataDir, String dataLogDir, String convPolicy){
        String uri = "file:" + SNAP_DIR + " = " + dataDir + " ; " + LOG_DIR + " = " + dataLogDir;
        if(convPolicy != null){
            uri += ";" + DB_FORMAT_CONV + " = " + convPolicy;
        }
        return uri.replace('\\', '/');
    }


    /**
     * Given the two directory files the method returns a well-formed
     * logfile provider URI. This method is for backward compatibility with the
     * existing code that only supports logfile persistence and expects these two
     * parameters passed either on the command-line or in the configuration file
     * @param dataDir
     * @param dataLogDir
     * @return
     */
    public static URI makeFileLoggerURL(File dataDir, File dataLogDir){
        return URI.create(makeURIString(dataDir.getPath(), dataLogDir.getPath(), null));
    }

    public static URI makeFileLoggerURL(File dataDir, File dataLogDir, String convPolicy){
        return URI.create(makeURIString(dataDir.getPath(), dataLogDir.getPath(), convPolicy));
    }


    /**
     * Creates a valid transaction log file name.
     * @param zxid
     * @return
     */
    public static String makeLogName(long zxid){
        return "log." + Long.toHexString(zxid);
    }

    /**
     * Creates a snapshot file name
     * @param zxid used as a suffix
     * @return file name
     */
    public static String makeSnapshotName(long zxid){
        return "snapshot." + Long.toHexString(zxid);
    }

    /**
     * Extracts snapshot directory property value from the container.
     *
     * @param props properties container
     * @return file representing the snapshot directory
     */
    public static File getSnapDir(Properties props){
        return new File(props.getProperty(SNAP_DIR));
    }

    /**
     * Extracts transaction log directory property value from the container
     *
     * @param props properties container
     * @return file representing the txn log directory
     */
    public static File getLogDir(Properties props){
        return new File(props.getProperty(LOG_DIR));
    }


    /**
     * Extracts the value of the dbFormatConversion attribute
     * @param props properties container
     * @return value of the dbFormatConversion attribute
     */
    public static String getFormatConversionPolicy(Properties props){
        return props.getProperty(DB_FORMAT_CONV);
    }

    /**
     * Extracts zxid from the file name. The file name should have been created
     * using one of the makeLogName or makeSnapshotName
     *
     * @param name the file name to parse
     * @param prefix the file name prsfix (snapshot or log)
     * @return zxid
     */
    public static long getZxidFromName(String name, String prefix){
        long zxid = -1;
        String nameParts[] = name.split("\\.");
        if(nameParts.length == 2 && nameParts[0].equals(prefix)){
            try{
                zxid = Long.parseLong(nameParts[1], 16);
            }catch (Exception e){}
        }
        return zxid;
    }


    /**
     * Verifies that the file is a valid snapshot. Snapshot may be invalid if
     * it's incomplete as in a situation when the server dies while in the process
     * of storing a snapshot. Any file that is not a snapshot is also
     * an invalid snapshot
     * @param f file to verify
     * @return true if the snapshot is valid
     * @throws IOException
     */
    public static boolean isValidSnapshot(File f) throws IOException{
        if(f == null || Util.getZxidFromName(f.getName(), "snapshot") == -1){
            return false;
        }

        // Check for a valid snapshot
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        try{
            // including the header and the last / bytes
            // the snapshot should be at least 10 bytes
            if(raf.length() < 10){
                return false;
            }
            raf.seek(raf.length() - 5);
            byte bytes[] = new byte[5];
            int readlen = 0;
            int l;
            while(readlen < 5&&
                    (l = raf.read(bytes, readlen, bytes.length - readlen)) >= 0){
                readlen += l;
            }
            if(readlen != bytes.length){
                LOG.info("Invalid snapshot " + f
                        + " too short, len = " + readlen);
                return false;
            }

            ByteBuffer bb = ByteBuffer.wrap(bytes);
            int len = bb.getInt();
            byte b = bb.get();
            if(len != 1 || b != '/'){
                LOG.info("Invalid snapshot " + f + " len = " + len + " byte = " + (b & 0xff));
                return false;
            }
        }finally {
            raf.close();
        }

        return true;
    }

    /**
     * Grows the file to the specified number of bytes. This only happenes if
     * the current file position is sufficiently close(less than 4k) to end of
     * file
     * @param f
     * @param currentSize
     * @param preAllocSize
     * @return
     * @throws IOException
     */
    public static long padLogFile(FileOutputStream f, long currentSize,
                                  long preAllocSize) throws IOException{
        long position = f.getChannel().position();
        if(position + 4096 >= currentSize){
            currentSize = currentSize + preAllocSize;
            fill.position(0);
            f.getChannel().write(fill, currentSize - fill.remaining());
        }
        return currentSize;
    }

    /**
     * Reads a transaction entry from the input archive
     * @param ia archive to read from
     * @return null if the entry is corrupted or EOF has been reached; a buffer
     *              (possible empty) containing serialized transaction record
     * @throws IOException
     */
    public static byte[] readTxnBytes(InputArchive ia) throws IOException{
        try{
            byte[] bytes = ia.readBuffer("txnEntry");
            // Since we preallocate, we define EOF to be an
            // empty transaction
            if(bytes.length == 0){
                return bytes;
            }
            if(ia.readByte("EOF") != 'B'){
                LOG.error("Last transaction was partial");
                return null;
            }
        }catch (EOFException e){}
        return null;
    }

    /**
     * Serializes transaction header and transaction data into a byte buffer
     * @param hdr transaction header
     * @param txn transaction data
     * @return serialized transaction record
     * @throws IOException
     */
    public static byte[] marshallTxnEntry(TxnHeader hdr, Record txn) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputArchive boa = BinaryOutputArchive.getArchive(baos);

        hdr.serialize(boa, "hdr");
        if(txn != null){
            txn.serialize(boa, "txn");
        }
        return baos.toByteArray();
    }

    /**
     * Write the serialized transaction record to the output archive
     * @param oa output archive
     * @param bytes serialized transaction record
     * @throws IOException
     */
    public static void writeTxnBytes(OutputArchive oa, byte[] bytes) throws IOException{
        oa.writeBuffer(bytes, "txnEntry");
        oa.writeByte((byte) 0x42, "EOR"); // 'B'
    }


    /**
     * Compare file file names of form "prefix.version" Sort order result
     * returned in order of version
     */
    private static class DataDirFileComparator implements Comparator<File>, Serializable{

        private static final long serialVersionUID = 8627067904111909759L;

        private String prefix;
        private boolean ascending;

        public DataDirFileComparator(String prefix, boolean ascending) {
            this.prefix = prefix;
            this.ascending = ascending;
        }

        @Override
        public int compare(File o1, File o2) {
            return 0;
        }
    }

    /**
     * Sort the list of files. Recency as determined by the version component
     * of the file name
     *
     * @param files array of files
     * @param prefix files not matching this prefix are assumed to have a version = -1
     * @param ascending true sorted in ascending order, false results in descending order
     * @return sorted input files
     */
    public static List<File> sortDataDir(File[] files, String prefix, boolean ascending){
        if(files == null){
            return new ArrayList<File>(0);
        }
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new DataDirFileComparator(prefix, ascending));
        return fileList;
    }
}
