package com.lami.tuomatuo.mq.jafka.log;

/**
 * Created by xjk on 2016/10/9.
 */
public interface LogSegmentFilter {
    boolean filter(LogSegment segment);
}
