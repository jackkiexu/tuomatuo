package com.lami.tuomatuo.mq.zookeeper;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class Version implements   org.apache.zookeeper.version.Info {

    public static int getRevision(){
        return REVISION;
    }

    public static String getBuildDate(){
        return BUILD_DATE;
    }

    public static String getVersion() {
        return MAJOR + "." + MINOR + "." + MICRO
                + ((QUALIFIER == null) ? "" : "-") + QUALIFIER;
    }

    public static String getVersionRevision(){
        return getVersion() + "-" + getRevision();
    }

    public static String getFullVersion(){
        return getVersionRevision() + ", built on " + getBuildDate();
    }

    public static void printUsage(){
        System.out
                .print("Usage:\tjava -cp ... org.apache.zookeeper.Version "
                        + "[--full | --short | --revision],\n\tPrints --full version "
                        + "info if no arg specified.");
        System.exit(1);
    }


}
