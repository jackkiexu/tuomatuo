package com.lami.tuomatuo.mq.netty.channel;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/9/22.
 */
public class DefaultChannelFuture implements ChannelFuture {

    private static final Logger logger = Logger.getLogger(DefaultChannelFuture.class);

    private static final int DEAD_LOCK_CHECK_INTERVAL = 5000;
    private static Throwable CANCELLED = new Throwable();

    private Channel channel;
    private boolean cancellable;

    private ChannelFutureListener firstListener;
    private List<ChannelFutureListener> otherListener;
    private boolean done;
    private Throwable cause;
    private int waiters;

    public DefaultChannelFuture(Channel channel, boolean cancellable) {
        this.channel = channel;
        this.cancellable = cancellable;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isCancelled() {
        return cause == CANCELLED;
    }

    public boolean isSuccess() {
        return cause == null;
    }

    public synchronized Throwable getCause() {
        if(cause != CANCELLED){
            return cause;
        }else{
            return null;
        }
    }

    public boolean cancel() {
        if(!cancellable){
            return false;
        }
        synchronized (this){
            // Allow only once
            if(done){
                return false;
            }
            cause = CANCELLED;
            done = true;
            if(waiters > 0){
                this.notifyAll();
            }
        }

        notifyListeners();
        return true;
    }

    private boolean await0(long timeoutMillis, boolean interruptable) throws InterruptedException{
        long startTime = timeoutMillis <= 0? 0 : System.currentTimeMillis();
        long waitTime = timeoutMillis;

        synchronized (this){
            if(done){
                return done;
            }else if(waitTime <= 0){
                return done;
            }

            waiters++;
            try {
                for(;;){
                    try {
                        this.wait(Math.min(waitTime, DEAD_LOCK_CHECK_INTERVAL));
                    } catch (InterruptedException e) {
                        if(interruptable){
                            throw e;
                        }
                    }

                    if(done){
                        return true;
                    }else{
                        waitTime = timeoutMillis - (System.currentTimeMillis() - startTime);
                        if(waitTime <= 0){
                            return done;
                        }
                    }
                }
            } finally {
                waiters++;
                if(!done){
                    checkDeadLock();
                }
            }

        }
    }

    private void checkDeadLock(){

    }

    public void setSuccess() {
        synchronized (this){
            // Allow only once
            if(done){
                return;
            }
            done = true;
            if(waiters > 0){
                this.notifyAll();
            }
        }

        notifyListeners();
    }

    public void setFailure(Throwable cause) {
        synchronized (this){
            // Allow only once
            if(done){
                return;
            }

            this.cause = cause;
            done = true;
            if(waiters > 0){
                this.notifyAll();
            }
        }
        notifyListeners();
    }

    public void addListener(ChannelFutureListener listener) {
        if(listener == null){
            throw new NullPointerException("listner is null");
        }

        boolean notifyNow = false;
        synchronized (this){
            if(done){
                notifyNow = true;
            }else{
                if(firstListener == null){
                    firstListener = listener;
                }else{
                    if(otherListener == null){
                        otherListener = new ArrayList<ChannelFutureListener>();
                    }
                    otherListener.add(listener);
                }
            }

        }

        if(notifyNow){
            notifyListener(listener);
        }
    }

    public void removeListener(ChannelFutureListener listener) {
        if(listener == null) throw new NullPointerException("listener");

        synchronized (this){
            if(!done){
                if(listener == firstListener){
                    if(otherListener != null && !otherListener.isEmpty()){
                        firstListener = otherListener.remove(0);
                    }else{
                        firstListener = null;
                    }
                }else if(otherListener != null){
                    otherListener.remove(listener);
                }
            }
        }
    }

    public ChannelFuture await() throws InterruptedException {
        synchronized (this){
            while(!done){
                waiters++;
                try {
                    this.wait(DEAD_LOCK_CHECK_INTERVAL);
                    checkDeadLock();
                } finally {
                    waiters--;
                }
            }
        }
        return this;
    }

    public ChannelFuture awaitUninterruptibly() {
        synchronized (this){
            while(!done){
                waiters++;
                try {
                    this.wait(DEAD_LOCK_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    waiters--;
                    if(!done){
                        checkDeadLock();
                    }
                }
            }
        }
        return this;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await(unit.toMillis(timeout));
    }

    public boolean await(long timoutMillis) throws InterruptedException {
        return await0(timoutMillis, true);
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return awaitUninterruptibly(unit.toMillis(timeout));
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return await0(timeoutMillis, false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    private void notifyListeners(){
        /** There won't be any visibility problen or concurrent modification
         *  because 'ready' flag will be checked against both addListener and
         *  removeListener calls
         */
        if(firstListener != null){
            notifyListener(firstListener);
            firstListener = null;
            if(otherListener != null && otherListener.size() != 0){
                for(ChannelFutureListener l : otherListener){
                    notifyListener(l);
                }
                otherListener = null;
            }
        }
    }

    private void notifyListener(ChannelFutureListener l){
        try {
            l.operationComplete(this);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception was thrown by" +
            ChannelFutureListener.class.getName());
        }
    }

}
