package com.manyi.dcm.lbdatasource;

/**
 * Created by xujiankang on 2016/9/9.
 */
public class ReadWriteDataSourceDecision {

    public enum DataSourceType{
        write,read;
    }

    private static final ThreadLocal<DataSourceType> holder = new ThreadLocal<DataSourceType>();

    public static void makeWrite(){
        holder.set(DataSourceType.write);
    }

    public static void markRead(){
        holder.set(DataSourceType.read);
    }

    public static void reset(){
        holder.set(null);
    }

    public static boolean isChoiceNone(){
        return null == holder.get();
    }

    public static boolean isChoiceWrite(){
        return DataSourceType.write == holder.get();
    }

    public static boolean isChoiceRead(){
        return DataSourceType.read == holder.get();
    }

}
