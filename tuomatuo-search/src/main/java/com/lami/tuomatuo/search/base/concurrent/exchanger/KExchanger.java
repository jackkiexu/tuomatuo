package com.lami.tuomatuo.search.base.concurrent.exchanger;

/**
 * Created by xujiankang on 2017/2/6.
 */
public class KExchanger<V> {


    @sun.misc.Contended static final class Node {
        int index;              // Arena index
        int bound;              // Last recorded value of Exchanger bound
        int collides;          // Number of CAS failures at current bound
        int hash;               // Pseudo-random for spins
        Object item;            // This thread's current item
        volatile Object match; // Item provided by releasing thread
        volatile Thread parked;// Set to this thread when parked, else null
    }

    /** The corresponding thread local class */
    static final class Participant extends ThreadLocal<Node> {
        @Override
        protected Node initialValue() {
            return new Node();
        }
    }

    /** Per-thread state */
    private  Participant participant;

    /**
     * Elimination array; null until enabled (with slotExchange)
     * Element accesses use emulation of volatile gets and CAS
     */
    private volatile Node[] arena;

    /** Slot used until contention detected */
    private volatile Node slot;

    /**
     * The index of the largest valid arena position, OR'ed with SEQ
     * number in high bits, incremented on each update. The initial
     * update from 0 to SEQ is used to ensure that the arena array is
     * constructed only once
     */
    private volatile int bound;


    /**
     * Exchange function when arenas enabled. See above for explanation
     *
     * @param item the (non-null) item to exchange
     * @param timed true if the wait is timed
     * @param ns if timed, the maximum wait time, else Ol
     * @return the other thread's item; or null if interrupted; or
     *      TIME_OUT if timed and timed out
     */
    private final Object arenaExchange(Object item, boolean timed, long ns){
        Node[] a = arena;
        Node p = participant.get();


        return null;
    }

}
