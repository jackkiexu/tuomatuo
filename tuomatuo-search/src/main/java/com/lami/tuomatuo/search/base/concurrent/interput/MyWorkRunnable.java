package com.lami.tuomatuo.search.base.concurrent.interput;

/**
 * Created by xujiankang on 2016/5/11.
 */
public abstract class MyWorkRunnable implements Runnable{

    volatile  Thread mTheThread = null;


    public void start(){
        mTheThread = new Thread(this);
        mTheThread.start();
    }



}
