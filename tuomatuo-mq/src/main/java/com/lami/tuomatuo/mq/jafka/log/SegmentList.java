package com.lami.tuomatuo.mq.jafka.log;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by xjk on 2016/10/9.
 */
public class SegmentList {

    AtomicReference<List<LogSegment>> contents;

    public SegmentList(List<LogSegment> accum) {
        this.contents = new AtomicReference<List<LogSegment>>(accum);
    }

    /**
     * Delete the first n items from the list
     * @return
     */
    public List<LogSegment>

    public List<LogSegment> getView(){
        return contents.get();
    }

    public static final int MaxAttempts = 20;

    public LogSegment getLastView(){
        List<LogSegment> views = getView();
        return views.get(views.size() - 1);
    }
}
