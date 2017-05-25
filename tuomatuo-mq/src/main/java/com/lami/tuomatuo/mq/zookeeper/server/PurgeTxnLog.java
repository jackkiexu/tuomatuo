package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * this class is used to clean up the
 * snapshot and data log dir's. This is usually
 * run as a cronjob on the zookeeper server machine
 * Invocation of this class will clean up the datalogdir
 * files and snapdir files keeping the last "-n" snapshot files
 * and the corresponding logs
 *
 * Created by xujiankang on 2017/3/19.
 */
public class PurgeTxnLog {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeTxnLog.class);

    private static final String COUNT_ERR_MSG = "count should be greater than or equal to 3";

    private static final String PREFIX_SNAPSHOT = "snapshot";
    private static final String PREFIX_LOG = "log";

    static void printUsage(){
        System.out.println("Usage");
        System.out.println("PurgeTxnLog dataLogDir [snapDir] -n count");
        System.out.println("\tdataLogDir -- path to the txn log directory");
        System.out.println("\tsnapDir -- path to the snapshot directory");
        System.out.println("\tcount -- the number of old snaps/logs you want"
                        + " to keep, value should be greater than or equal to 3 ");
    }


    public static void purge(File dataDir, File snapDir, int num) throws IOException{
        if(num < 3){
            throw new IllegalArgumentException(COUNT_ERR_MSG);
        }

        FileTxnSnapLog txnLog = new FileTxnSnapLog(dataDir, snapDir);
        List<File> snaps = txnLog.findNRecentSnapshots(num);
        int numSnaps = snaps.size();
        if(numSnaps > 0){
            purgeOlderSnapshots(txnLog, snaps.get(numSnaps - 1));
        }
    }

    // VisibleForTesting
    static void purgeOlderSnapshots(FileTxnSnapLog txnLog, File snapShot){
        final long leastZxidToBeRetain = Util.getZxidFromName(snapShot.getName(), PREFIX_SNAPSHOT);


    }

    /**
     * validates file existence and returns the file
     * @param path
     * @return
     */
    private static File validateAndGetFile(String path){
        File file = new File(path);
        if(!file.exists()){
            System.err.println("Path '" + file.getAbsolutePath() + "' does not exist");
            printUsageThenExit();
        }
        return file;
    }


    /**
     * Returns integer if parsed successfully and it is valid otherwise prints
     * error and usage and then exits
     *
     * @param number
     * @return
     */
    private static int validateAndGetCount(String number){
       int result = 0;
        try{
            result = Integer.parseInt(number);
            if(result < 3){
                System.err.println(COUNT_ERR_MSG);
                printUsageThenExit();
            }
        }catch (NumberFormatException e){
            System.err.println("'" + number + "' can not be parsed to integer.");
            printUsageThenExit();
        }
        return result;
    }


    private static void printUsageThenExit(){
        printUsage();
        System.exit(1);
    }
}
