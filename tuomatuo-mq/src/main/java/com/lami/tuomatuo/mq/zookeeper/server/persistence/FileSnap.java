package com.lami.tuomatuo.mq.zookeeper.server.persistence;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.persistence.FileHeader;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

/**
 * This class implements the snapshot interface
 * it is reponsible for storing, serializing
 * and deserializing the right snapshot
 * and provides access to the snapshots
 *
 * Created by xjk on 3/18/17.
 */
public class FileSnap implements SnapShot {

    File snapDir;
    private volatile boolean close = false;
    private static final int VERSION = 2;
    private static final long dbId = -1;
    private static final Logger LOG = LoggerFactory.getLogger(FileSnap.class);
    public final static int SNAP_MAGIC =
            ByteBuffer.wrap("ZKSN".getBytes()).getInt();

    public FileSnap(File snapDir) {
        this.snapDir = snapDir;
    }


    /**
     * deserialize a data tree from the most recent snapshot
     * @param dt the datatree to be deserialized into
     * @param sessions the sessions to be deserialized into
     * @return the zxid of the snapshot
     * @throws IOException
     */
    public long deserialize(DataTree dt, Map<Long, Integer> sessions) throws IOException {

        // we run through 100 snapshots (not all of them)
        // if we cannot get it running within 100 snapshots
        // we should  give up
        List<File> snapList = findNValidSnapshots(100);
        if (snapList.size() == 0) {
            return -1L;
        }
        File snap = null;
        boolean foundValid = false;
        for (int i = 0; i < snapList.size(); i++) {
            snap = snapList.get(i);
            InputStream snapIS = null;
            CheckedInputStream crcIn = null;
            try {
                LOG.info("Reading snapshot " + snap);
                snapIS = new BufferedInputStream(new FileInputStream(snap));
                crcIn = new CheckedInputStream(snapIS, new Adler32());
                InputArchive ia = BinaryInputArchive.getArchive(crcIn);
                deserialize(dt,sessions, ia);
                long checkSum = crcIn.getChecksum().getValue();
                long val = ia.readLong("val");
                if (val != checkSum) {
                    throw new IOException("CRC corruption in snapshot :  " + snap);
                }
                foundValid = true;
                break;
            } catch(IOException e) {
                LOG.warn("problem reading snap file " + snap, e);
            } finally {
                if (snapIS != null)
                    snapIS.close();
                if (crcIn != null)
                    crcIn.close();
            }
        }
        if (!foundValid) {
            throw new IOException("Not able to find valid snapshots in " + snapDir);
        }
        dt.lastProcessedZxid = org.apache.zookeeper.server.persistence.Util.getZxidFromName(snap.getName(), "snapshot");
        return dt.lastProcessedZxid;
    }

    /**
     * deserialize the datatree from an input archive
     * @param dt the datatree to be serialized into
     * @param sessions the sessions to be filled up
     * @param ia the input archive to restore from
     * @throws IOException
     */
    public void deserialize(DataTree dt, Map<Long, Integer> sessions, InputArchive ia) throws IOException{
        FileHeader header = new FileHeader();
        header.deserialize(ia, "fileheader");
        if(header.getMagic() != SNAP_MAGIC){
            throw new IOException("mismatching magic headers" + header.getMagic() + " != " + FileSnap.SNAP_MAGIC);
        }
        SerializeUtils.deserializeSnapshot(dt, ia, sessions);
    }


    @Override
    public long deserialize(com.lami.tuomatuo.mq.zookeeper.server.DataTree dt, Map<Long, Integer> sessions) throws IOException {
        return 0;
    }

    @Override
    public void serialize(com.lami.tuomatuo.mq.zookeeper.server.DataTree dt, Map<Long, Integer> sessions, File name) throws IOException {

    }

    /**
     * find the most recent snapshot in the database
     * @return the file containing the most recent snapshot
     * @throws IOException
     */
    public File findMostRecentSnapshot() throws IOException {
        List<File> files = findNValidSnapshots(1);
        if(files.size() == 0){
            return null;
        }
        return files.get(0);
    }


    /**
     * find the last (maybe) valid n snapshots. this does some
     * minor checks on the validity of the snapshots. It just
     * checks for / at the end of the snapshot. This does
     * not mean that the snapshot is truly valid but is
     * valid with a high probability. also, the most recent
     * will be first on the list.
     * @param n the number of most recent snapshots
     * @return the last n snapshots (the number might be
     * less than n in case enough snapshots are not available).
     * @throws IOException
     */
    private List<File> findNValidSnapshots(int n) throws IOException {
        List<File> files = org.apache.zookeeper.server.persistence.Util.sortDataDir(snapDir.listFiles(),"snapshot", false);
        int count = 0;
        List<File> list = new ArrayList<File>();
        for (File f : files) {
            // we should catch the exceptions
            // from the valid snapshot and continue
            // until we find a valid one
            try {
                if (org.apache.zookeeper.server.persistence.Util.isValidSnapshot(f)) {
                    list.add(f);
                    count++;
                    if (count == n) {
                        break;
                    }
                }
            } catch (IOException e) {
                LOG.info("invalid snapshot " + f, e);
            }
        }
        return list;
    }


    /**
     * find the last n snapshot. This does not have
     * any checks if the snapshot might be valid or not
     *
     * @param n number of most recent snapshots
     * @return the last n snapshots
     * @throws IOException
     */
    public List<File> findNRecentSnapshots(int n) throws IOException{
        List<File> files = Util.sortDataDir(snapDir.listFiles(), "snapshot", false);
        int count = 0;
        List<File> list = new ArrayList<>();
        for(File f : list){
            if(count == n){
                break;
            }
            if(Util.getZxidFromName(f.getName(), "snapshot") != -1){
                count++;
                list.add(f);
            }
        }

        return list;
    }

    /**
     * serialize the datatree and sessions
     *
     * @param dt the datatree to be serialized
     * @param sessions the sessions to be serialized
     * @param oa the output archive to serialize into
     * @param header the header of this snapshot
     * @throws IOException
     */
    protected void serialize(DataTree dt, Map<Long, Integer> sessions, OutputArchive oa, FileHeader header)
            throws IOException{
        // this is really a programmatic error and not something that can
        // happen at runtime
        if(header == null){
            throw new IllegalStateException("Snapshot's not open for writing: uninitialized header");
        }
        header.serialize(oa, "fileheader");
        SerializeUtils.serializeSnapshot(dt, oa, sessions);
    }


    /**
     * serialize the datatree and session into the file snapshot
     *
     * @param dt the datatree to be serialized
     * @param sessions the sessions to be serialized
     * @param name the file to store snapshot into
     * @throws IOException
     */
    public void serialize(DataTree dt, Map<Long, Integer> sessions, File snapShot) throws IOException {
        if(!close){
            OutputStream sessOS = new BufferedOutputStream(new FileOutputStream(snapShot));
            CheckedOutputStream crcOut = new CheckedOutputStream(sessOS, new Adler32());
            // CheckedOutputStream cout = new CheckedOutputStream()
            OutputArchive oa = BinaryOutputArchive.getArchive(crcOut);
            FileHeader header = new FileHeader(SNAP_MAGIC, VERSION, dbId);
            serialize(dt, sessions, oa, header);
            long val = crcOut.getChecksum().getValue();
            oa.writeLong(val, "val");
            oa.writeString("/", "path");
            sessOS.flush();
            crcOut.close();
            sessOS.flush();
        }
    }

    /**
     * synchronized close just so that if serialize is in place
     * the close operation will block and will wait till serialize
     * is done and will set the close flag
     * @throws IOException
     */
    @Override
    public synchronized void close() throws IOException {
        close = true;
    }
}
