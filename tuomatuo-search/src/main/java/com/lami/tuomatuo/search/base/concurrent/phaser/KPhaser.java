package com.lami.tuomatuo.search.base.concurrent.phaser;

/**
 * A reusable synchronization barrier, similar in functionality to
 * {@link java.util.concurrent.CyclicBarrier} and
 * {@link java.util.concurrent.CountDownLatch}
 * but supporting more flexible usage.
 *
 * <p>
 *      <b>Registration</b> Unlike the case for other barriers, the
 *      number of parties <em>registered</em> to synchronize on a phaser
 *      may vary over time. Tasks may be registered at any time (using
 *      methods {@link #"register}), {@link #"bulkRegister}, or forms of
 *      constructors establishing initial numbers of parties, and
 *      optionally deregistered upon any arrival using {@link #"arriveAndDeregister}
 *      As is the case with most basic
 *      synchronization constructs, registration and deregistration affect
 *      only internal counts: they do not establish any further internal
 *      bookkeeping, so introduce such bookkeeping by subclassing this class
 * </p>
 *
 * <p>
 *     <b>Synchronization</b> Like a {@code CyclicBarrier} a {@code
 *     Phaser}, may be repeatedly awaited. Method {@link #"arriveAndAwaitAdvance}
 *     has effect analogous to {@link java.util.concurrent.CyclicBarrier} Each
 *     generation of a phaser has an associated phase number. The phase
 *     number starts at zero, and advances when all parties arrive at the pharser,
 *     wrapping around to zero after reaching {@code Integer.MAX_VALUE}
 *     The use of phase numbers enables independent
 *     control
 *     of actions upon arrival at a phaser and upon awaiting
 *     others, via two kinds of methods that may be invoked by any
 *     registered party
 * </p>
 *
 * <li>
 *      <b>Arrival.</b> Methods {@link #"awaitAdvance} requires an
 *      argument indicating an arrival phase number, and returns when
 *      the phaser advances to (or is already at) a different phase
 *      Unlike similar constructions using {@code CyclicBarrier}
 *      method {@code awaitAdvance} continues to wait even if the
 *      waiting thread is interrupted. Interruptible and timeout
 *      versions are also available, but
 * </li>
 *
 * Created by xjk on 1/4/17.
 */
public class KPhaser {

    private volatile long state;

    private static final int MAX_PARTIES            = 0xffff;
    private static final int MAX_PHASE               = Integer.MAX_VALUE;


}
