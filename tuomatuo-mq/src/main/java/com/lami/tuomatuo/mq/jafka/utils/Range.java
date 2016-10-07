package com.lami.tuomatuo.mq.jafka.utils;

/**
 *
 * A generic range value with a start and end
 *
 * Created by xjk on 10/1/16.
 */
public interface Range {

    /** The first index in the range  */
    long start();

    /** The total number of the indexes in the range */
    long size();

    /** return true if the range is empty */
    boolean isEmpty();

    /** if value is in tha range */
    boolean cantains(long value);

    String toString();


    public static abstract class AbstractRange implements Range{
        public boolean isEmpty(){
            return size() == 0;
        }

        public boolean contains(long value){
            long size = size();
            long start = start();
            return ((size == 0 && value == start)
            || (size > 0 && value >= start && value <= start + size - 1));
        }

        @Override
        public String toString() {
            return "(start = " + start() + ", size=" + size() + ")";
        }
    }

}
