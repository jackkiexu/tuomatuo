package com.lami.tuomatuo.mq.jafka.log;

import javax.swing.text.Segment;
import java.util.ArrayList;
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
    public List<LogSegment> trunc(int newStart){
        if(newStart < 0){
            throw new IllegalArgumentException("Staring index must be positive");
        }
        while(true){
            List<LogSegment> curr = contents.get();
            int newLength = Math.max(curr.size() - newStart, 0);
            List<LogSegment> updatedList = new ArrayList<LogSegment>(curr.subList(Math.min(newStart, curr.size() - 1), curr.size()));
            if(contents.compareAndSet(curr, updatedList)){
                return curr.subList(0, curr.size() - newLength);
            }
        }
    }

    public List<LogSegment> getView(){
        return contents.get();
    }

    public static final int MaxAttempts = 20;

    public LogSegment getLastView(){
        List<LogSegment> views = getView();
        return views.get(views.size() - 1);
    }
}
