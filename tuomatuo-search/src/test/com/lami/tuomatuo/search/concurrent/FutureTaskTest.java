package com.lami.tuomatuo.search.concurrent;

import com.lami.tuomatuo.search.base.concurrent.future.xjk.KFutureTask;
import org.junit.Test;

/**
 * Created by xjk on 12/18/16.
 */
public class FutureTaskTest {

    @Test
    public void enqueue(){
        KFutureTask<?> futureTask = new KFutureTask<>();

        futureTask.enqueue(new KFutureTask.WaiterNode(Thread.currentThread()));
    }

}
