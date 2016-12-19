package com.lami.tuomatuo.search.base.concurrent.aqs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by xujiankang on 2016/12/19.
 */
public class Mutex implements Lock, java.io.Serializable {

    // The sync object does all the hard work. We just forward to it
    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean isLocked(){
        return sync.inHeldExclusively();
    }

    public boolean hasQueuedThreads(){
        return sync.hasQueuedThreads();
    }



    // internal helper class
    static class Sync extends AbstractQueuedSynchronizer{

        // report whether in locked state
        protected boolean inHeldExclusively(){
            return getState() == 1;
        }

        // Acquire the lock if state is zero
        public boolean tryAcquire(int acquires){
            assert acquires == 1; // Otherwise unsed
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        // Releses the lock by setting state to zero
        protected boolean tryRelease(int release){
            assert release == 1; // Otherwise unused
            if(getState() == 0){
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        // Provides a Condition
        Condition newCondition(){
            return new ConditionObject();
        }

        // Deserializes properly
        private void readObject(ObjectInputStream s)throws IOException, ClassNotFoundException{
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

}
