package com.lami.tuomatuo.search.base.concurrent.forkjoinpool;

/**
 * Created by xjk on 2/26/17.
 */
public class KForkJoinPool {


    /**
     * Queues supporting work-stealing as well as extenal task
     * submission. See above for descriptions and algorithms
     * Performance on most platforms is very sensitive to placement of
     * instances of both WorkQueues and their arrays -- we absolutely
     * do not want multiple WorkQueue instances or multiple queue
     * arrays sharing cache lines. The @Contended annotation alerts
     * JVMs to try to keep instances apart
     *
     */
    @sun.misc.Contended
    static final class WorkQueue{

        /**
         * Capacity of work-stealing queue array upon initialization.
         * Must be a powere of two; at least 4, but should be larger to
         * reduce or eliminate cacheline sharing among queues.
         * Currently. it is much larger, as a partial workaround for
         * the fact the JVMs often place arrays in locations that
         * share GC bookkeeping (especially cardmarks) such that
         * per-write accesses encounter serious memory contention
         */
        static final int INITIAL_QUEUE_CAPACITY = 1 << 13;

        /**
         * Maximum size for queue arrays. Must be a power of two less
         * than or equal to 1 << (31 - width of array entry) to ensure
         * lack of wraparound of index calculations, but defined to a
         * value a bit less than this to help users trap runaway
         * programs before sturating systems.
         */
        static final int MAXIMUM_QUEUE_CAPACITY = 1 << 26; // 64M

        // Instance fields
        volatile int scanState;                 // versioned, < 0 inactive; odd:scanning
        int stackPred;                          // pool stack (ctl) predecessor
        int nsteads;                            // number of steals
        int hint;                               // randomization and stealer index hint
        int config;                             // pool index and mode
        volatile int qlock;                     // 1: locked, < 0: terminate; else 0
        volatile int base;                      // index of next slot for poll
        int top;                                // index of next slot for push
        KForkJoinTask<?>[] array;               // the element (initially unallocated)
        final KForkJoinPool pool;               // the containing pool (may be null)
        final KForkJoinWorkerThread owner;      // owning thread or null if shared
        volatile Thread parker;                 // == owner during call to park; else null
        volatile KForkJoinTask<?> currentJoin;  // task being joined in awaitJoin
        volatile KForkJoinTask<?> currentSteal; // mainly used by helpStealer


        public WorkQueue(KForkJoinPool pool, KForkJoinWorkerThread owner) {
            this.pool = pool;
            this.owner = owner;
            // Place indices in the center of array (that is not yet allocated)
            base = top = INITIAL_QUEUE_CAPACITY >>> 1;
        }
    }
}
